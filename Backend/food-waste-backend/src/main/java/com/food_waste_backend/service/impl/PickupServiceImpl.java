package com.food_waste_backend.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.food_waste_backend.Entity.FoodDonation;
import com.food_waste_backend.Entity.FoodRequest;
import com.food_waste_backend.Entity.Notification;
import com.food_waste_backend.Entity.PickupRequest;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.Entity.Volunteer;
import com.food_waste_backend.dto.PickupRequestDto;
import com.food_waste_backend.dto.PickupResponse;
import com.food_waste_backend.enums.DonationStatus;
import com.food_waste_backend.enums.NotificationType;
import com.food_waste_backend.enums.PickupStatus;
import com.food_waste_backend.enums.RequestStatus;
import com.food_waste_backend.enums.UserRole;
import com.food_waste_backend.exception.BadRequestException;
import com.food_waste_backend.exception.ResourceNotFoundException;
import com.food_waste_backend.repository.FoodDonationRepository;
import com.food_waste_backend.repository.FoodRequestRepository;
import com.food_waste_backend.repository.NotificationRepository;
import com.food_waste_backend.repository.PickupRequestRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.repository.VolunteerRepository;
import com.food_waste_backend.service.PickupService;

@Service
public class PickupServiceImpl implements PickupService {

    private final PickupRequestRepository pickupRequestRepository;
    private final VolunteerRepository volunteerRepository;
    private final FoodDonationRepository foodDonationRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FoodRequestRepository foodRequestRepository;

    public PickupServiceImpl(
            PickupRequestRepository pickupRequestRepository,
            VolunteerRepository volunteerRepository,
            FoodDonationRepository foodDonationRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            FoodRequestRepository foodRequestRepository) {
        this.pickupRequestRepository = pickupRequestRepository;
        this.volunteerRepository = volunteerRepository;
        this.foodDonationRepository = foodDonationRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.foodRequestRepository = foodRequestRepository;
    }

    @Override
    @Transactional
    public PickupResponse createPickup(String email, PickupRequestDto request) {
        Volunteer volunteer = null;
        if (email != null && !email.isBlank()) {
            volunteer = volunteerRepository.findByUserEmail(email).orElse(null);
        }

        PickupRequest pickupRequest = new PickupRequest();
        pickupRequest.setVolunteer(volunteer);
        pickupRequest.setScheduledTime(request.getScheduledTime() != null ? request.getScheduledTime() : LocalDateTime.now().plusHours(2));
        pickupRequest.setStatus(PickupStatus.SCHEDULED);

        if (request.getDonationId() != null) {
            FoodDonation donation = foodDonationRepository.findById(request.getDonationId())
                    .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", request.getDonationId()));
            pickupRequest.setDonation(donation);
            pickupRequest.setPickupAddress(donation.getPickupAddress());
            pickupRequest.setContactPhone(donation.getContactPhone());

            // Check if there is an approved food request for this donation to link recipient NGO
            List<FoodRequest> requests = foodRequestRepository.findByDonationId(donation.getId());
            requests.stream()
                    .filter(r -> r.getStatus() == RequestStatus.APPROVED)
                    .findFirst()
                    .ifPresent(approvedReq -> {
                        pickupRequest.setFoodRequest(approvedReq);
                        if (approvedReq.getDeliveryNotes() != null) {
                            pickupRequest.setDeliveryAddress(approvedReq.getDeliveryNotes());
                        } else if (approvedReq.getNgo() != null && approvedReq.getNgo().getUser() != null) {
                            pickupRequest.setDeliveryAddress(approvedReq.getNgo().getNgoName() + " Redistribution Center");
                        }
                    });

            // Update donation status
            if (donation.getStatus() == DonationStatus.AVAILABLE) {
                donation.setStatus(DonationStatus.ACCEPTED);
                foodDonationRepository.save(donation);
            }

            // Notify volunteer if assigned
            if (volunteer != null && volunteer.getUser() != null) {
                Notification notification = new Notification();
                notification.setUser(volunteer.getUser());
                notification.setTitle("New Pickup Assigned");
                notification.setMessage("You have been assigned to pickup: " + donation.getTitle() + " at " + (donation.getPickupAddress() != null ? donation.getPickupAddress() : "Donor location"));
                notification.setType(NotificationType.PICKUP);
                notification.setIsRead(false);
                notificationRepository.save(notification);
            }
        }

        PickupRequest saved = pickupRequestRepository.save(pickupRequest);
        return mapToResponse(saved);
    }

    @Override
    public List<PickupResponse> getAllPickups() {
        return pickupRequestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PickupResponse> getMyPickups(String email) {
        if (email != null && !email.isBlank()) {
            Volunteer volunteer = volunteerRepository.findByUserEmail(email).orElse(null);
            if (volunteer != null) {
                return pickupRequestRepository.findByVolunteerId(volunteer.getId())
                        .stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());
            }

            // If caller is donor, return pickups for their donations
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null && user.getRole() == UserRole.DONOR) {
                return pickupRequestRepository.findAll().stream()
                        .filter(p -> p.getDonation() != null && p.getDonation().getDonor() != null &&
                                     p.getDonation().getDonor().getUser() != null &&
                                     p.getDonation().getDonor().getUser().getEmail().equals(email))
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());
            }

            // If caller is NGO, return pickups for their food requests
            if (user != null && user.getRole() == UserRole.NGO) {
                return pickupRequestRepository.findAll().stream()
                        .filter(p -> p.getFoodRequest() != null && p.getFoodRequest().getNgo() != null &&
                                     p.getFoodRequest().getNgo().getUser() != null &&
                                     p.getFoodRequest().getNgo().getUser().getEmail().equals(email))
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());
            }
        }

        return pickupRequestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PickupResponse> getAvailablePickups() {
        return pickupRequestRepository.findAll().stream()
                .filter(p -> (p.getVolunteer() == null || p.getStatus() == PickupStatus.SCHEDULED || p.getStatus() == PickupStatus.REQUESTED) &&
                             p.getStatus() != PickupStatus.COMPLETED && p.getStatus() != PickupStatus.CANCELLED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PickupResponse getPickupById(Long id) {
        PickupRequest pickup = pickupRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PickupRequest", id));
        return mapToResponse(pickup);
    }

    @Override
    @Transactional
    public PickupResponse updatePickupStatus(Long id, String status) {
        return updatePickupStatus(id, status, null, null);
    }

    @Override
    @Transactional
    public PickupResponse updatePickupStatus(Long id, String status, String email, String notes) {
        PickupRequest pickup = pickupRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PickupRequest", id));

        // Enforce authorization
        if (email != null && !email.isBlank()) {
            User caller = userRepository.findByEmail(email).orElse(null);
            if (caller != null && caller.getRole() != UserRole.ADMIN) {
                boolean isAssignedVolunteer = pickup.getVolunteer() != null &&
                        pickup.getVolunteer().getUser() != null &&
                        pickup.getVolunteer().getUser().getEmail().equals(email);
                boolean isDonor = pickup.getDonation() != null &&
                        pickup.getDonation().getDonor() != null &&
                        pickup.getDonation().getDonor().getUser() != null &&
                        pickup.getDonation().getDonor().getUser().getEmail().equals(email);
                boolean isNgo = pickup.getFoodRequest() != null &&
                        pickup.getFoodRequest().getNgo() != null &&
                        pickup.getFoodRequest().getNgo().getUser() != null &&
                        pickup.getFoodRequest().getNgo().getUser().getEmail().equals(email);

                if (!isAssignedVolunteer && !isDonor && !isNgo) {
                    throw new AccessDeniedException("Unauthorized to update this pickup task");
                }
            }
        }

        PickupStatus newStatus = PickupStatus.valueOf(status.toUpperCase().trim());
        pickup.setStatus(newStatus);

        if (notes != null && !notes.isBlank()) {
            String existingNotes = pickup.getHandoverNotes() != null ? pickup.getHandoverNotes() + "; " : "";
            pickup.setHandoverNotes(existingNotes + notes.trim());
        }

        LocalDateTime now = LocalDateTime.now();

        if (newStatus == PickupStatus.COLLECTED) {
            pickup.setCollectedTime(now);
            if (pickup.getDonation() != null) {
                pickup.getDonation().setStatus(DonationStatus.COLLECTED);
                foodDonationRepository.save(pickup.getDonation());

                // Notify donor that food was picked up
                if (pickup.getDonation().getDonor() != null && pickup.getDonation().getDonor().getUser() != null) {
                    Notification n = new Notification();
                    n.setUser(pickup.getDonation().getDonor().getUser());
                    n.setTitle("Food Collected by Volunteer");
                    n.setMessage("Your donation '" + pickup.getDonation().getTitle() + "' was collected and is en route to redistribution.");
                    n.setType(NotificationType.PICKUP);
                    n.setIsRead(false);
                    notificationRepository.save(n);
                }
            }
        } else if (newStatus == PickupStatus.COMPLETED) {
            pickup.setCompletedTime(now);
            if (pickup.getDonation() != null) {
                pickup.getDonation().setStatus(DonationStatus.COMPLETED);
                foodDonationRepository.save(pickup.getDonation());

                // Notify donor
                if (pickup.getDonation().getDonor() != null && pickup.getDonation().getDonor().getUser() != null) {
                    Notification n = new Notification();
                    n.setUser(pickup.getDonation().getDonor().getUser());
                    n.setTitle("Redistribution Complete");
                    n.setMessage("Your donation '" + pickup.getDonation().getTitle() + "' has been successfully delivered and redistributed!");
                    n.setType(NotificationType.DELIVERY);
                    n.setIsRead(false);
                    notificationRepository.save(n);
                }
            }

            // Update associated FoodRequest to COMPLETED
            if (pickup.getFoodRequest() != null) {
                pickup.getFoodRequest().setStatus(RequestStatus.COMPLETED);
                foodRequestRepository.save(pickup.getFoodRequest());

                if (pickup.getFoodRequest().getNgo() != null && pickup.getFoodRequest().getNgo().getUser() != null) {
                    Notification n = new Notification();
                    n.setUser(pickup.getFoodRequest().getNgo().getUser());
                    n.setTitle("Food Delivery Received");
                    n.setMessage("Delivery of surplus food for '" + (pickup.getDonation() != null ? pickup.getDonation().getTitle() : "Donation") + "' has been confirmed.");
                    n.setType(NotificationType.DELIVERY);
                    n.setIsRead(false);
                    notificationRepository.save(n);
                }
            }
        } else if (newStatus == PickupStatus.FAILED) {
            // Log issue and alert parties
            if (pickup.getDonation() != null && pickup.getDonation().getDonor() != null && pickup.getDonation().getDonor().getUser() != null) {
                Notification n = new Notification();
                n.setUser(pickup.getDonation().getDonor().getUser());
                n.setTitle("Delivery Exception Reported");
                n.setMessage("A delivery issue was reported for '" + pickup.getDonation().getTitle() + "': " + (notes != null ? notes : "Unable to complete pickup/delivery."));
                n.setType(NotificationType.SYSTEM);
                n.setIsRead(false);
                notificationRepository.save(n);
            }
        }

        PickupRequest updated = pickupRequestRepository.save(pickup);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public PickupResponse claimPickup(Long id, String email) {
        PickupRequest pickup = pickupRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PickupRequest", id));

        Volunteer volunteer = volunteerRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadRequestException("Volunteer profile not found for user: " + email));

        pickup.setVolunteer(volunteer);
        pickup.setStatus(PickupStatus.SCHEDULED);

        if (pickup.getScheduledTime() == null) {
            pickup.setScheduledTime(LocalDateTime.now().plusHours(2));
        }

        PickupRequest updated = pickupRequestRepository.save(pickup);

        // Notify volunteer
        Notification n = new Notification();
        n.setUser(volunteer.getUser());
        n.setTitle("Pickup Task Claimed");
        n.setMessage("You have claimed pickup task for '" + (pickup.getDonation() != null ? pickup.getDonation().getTitle() : "Food Donation") + "'.");
        n.setType(NotificationType.PICKUP);
        n.setIsRead(false);
        notificationRepository.save(n);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public PickupResponse assignVolunteer(Long id, Long volunteerId) {
        PickupRequest pickup = pickupRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PickupRequest", id));

        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", volunteerId));

        pickup.setVolunteer(volunteer);
        pickup.setStatus(PickupStatus.SCHEDULED);
        PickupRequest updated = pickupRequestRepository.save(pickup);

        if (volunteer.getUser() != null) {
            Notification n = new Notification();
            n.setUser(volunteer.getUser());
            n.setTitle("Assigned to Food Pickup");
            n.setMessage("You have been assigned to collect '" + (pickup.getDonation() != null ? pickup.getDonation().getTitle() : "Food") + "'.");
            n.setType(NotificationType.PICKUP);
            n.setIsRead(false);
            notificationRepository.save(n);
        }

        return mapToResponse(updated);
    }

    private PickupResponse mapToResponse(PickupRequest pickup) {
        PickupResponse response = new PickupResponse();
        response.setId(pickup.getId());
        response.setStatus(pickup.getStatus() != null ? pickup.getStatus().name() : null);
        response.setScheduledTime(pickup.getScheduledTime());
        response.setCollectedTime(pickup.getCollectedTime());
        response.setCompletedTime(pickup.getCompletedTime());
        response.setPickupAddress(pickup.getPickupAddress());
        response.setDeliveryAddress(pickup.getDeliveryAddress());
        response.setNotes(pickup.getNotes());
        response.setHandoverNotes(pickup.getHandoverNotes());

        if (pickup.getVolunteer() != null) {
            response.setVolunteerId(pickup.getVolunteer().getId());
            if (pickup.getVolunteer().getUser() != null) {
                response.setVolunteerName(pickup.getVolunteer().getUser().getName());
                response.setVolunteerPhone(pickup.getVolunteer().getUser().getPhone());
            }
        }

        Map<String, Object> donationMap = new HashMap<>();
        Map<String, Object> donorMap = new HashMap<>();
        if (pickup.getDonation() != null) {
            FoodDonation d = pickup.getDonation();
            response.setDonationId(d.getId());
            response.setDonationTitle(d.getTitle());
            response.setDescription(d.getDescription());
            response.setCategory(d.getCategory() != null ? d.getCategory().name() : null);
            response.setQuantity(d.getQuantity());
            response.setQuantityUnit(d.getQuantityUnit());

            if (response.getPickupAddress() == null || response.getPickupAddress().isBlank()) {
                response.setPickupAddress(d.getPickupAddress());
            }

            donationMap.put("id", d.getId());
            donationMap.put("title", d.getTitle());
            donationMap.put("description", d.getDescription());
            donationMap.put("category", d.getCategory());
            donationMap.put("quantity", d.getQuantity());
            donationMap.put("quantityUnit", d.getQuantityUnit());
            donationMap.put("pickupAddress", d.getPickupAddress());
            donationMap.put("safeUseDeadline", d.getSafeUseDeadline());
            donationMap.put("expiryTime", d.getExpiryTime());

            if (d.getDonor() != null && d.getDonor().getUser() != null) {
                response.setDonorName(d.getDonor().getOrganizationName() != null ? d.getDonor().getOrganizationName() : d.getDonor().getUser().getName());
                response.setDonorPhone(d.getContactPhone() != null ? d.getContactPhone() : d.getDonor().getUser().getPhone());

                donorMap.put("name", response.getDonorName());
                donorMap.put("phone", response.getDonorPhone());
                donorMap.put("email", d.getDonor().getUser().getEmail());
            }
        }
        response.setDonation(donationMap);
        response.setDonor(donorMap);

        Map<String, Object> ngoMap = new HashMap<>();
        if (pickup.getFoodRequest() != null) {
            FoodRequest fr = pickup.getFoodRequest();
            response.setFoodRequestId(fr.getId());
            if (fr.getNgo() != null) {
                response.setNgoName(fr.getNgo().getNgoName());
                if (fr.getNgo().getUser() != null) {
                    response.setNgoPhone(fr.getNgo().getUser().getPhone());
                }
                ngoMap.put("name", fr.getNgo().getNgoName());
                ngoMap.put("contactPerson", fr.getNgo().getContactPerson());
                ngoMap.put("phone", response.getNgoPhone());
            }
            if (response.getDeliveryAddress() == null && fr.getDeliveryNotes() != null) {
                response.setDeliveryAddress(fr.getDeliveryNotes());
            }
        }
        response.setNgo(ngoMap);

        return response;
    }
}
