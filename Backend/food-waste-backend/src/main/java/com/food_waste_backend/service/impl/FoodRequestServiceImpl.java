package com.food_waste_backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.food_waste_backend.Entity.Donor;
import com.food_waste_backend.Entity.FoodDonation;
import com.food_waste_backend.Entity.FoodRequest;
import com.food_waste_backend.Entity.Ngo;
import com.food_waste_backend.Entity.Notification;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.dto.FoodRequestDto;
import com.food_waste_backend.dto.FoodRequestResponse;
import com.food_waste_backend.enums.DonationStatus;
import com.food_waste_backend.enums.NotificationType;
import com.food_waste_backend.enums.RequestStatus;
import com.food_waste_backend.enums.UserRole;
import com.food_waste_backend.exception.ResourceNotFoundException;
import com.food_waste_backend.repository.DonorRepository;
import com.food_waste_backend.repository.FoodDonationRepository;
import com.food_waste_backend.repository.FoodRequestRepository;
import com.food_waste_backend.repository.NgoRepository;
import com.food_waste_backend.repository.NotificationRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.service.FoodRequestService;

@Service
public class FoodRequestServiceImpl implements FoodRequestService {

    private final FoodRequestRepository foodRequestRepository;
    private final NgoRepository ngoRepository;
    private final FoodDonationRepository foodDonationRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final com.food_waste_backend.repository.PickupRequestRepository pickupRequestRepository;
    private final com.food_waste_backend.repository.VolunteerRepository volunteerRepository;

    public FoodRequestServiceImpl(
            FoodRequestRepository foodRequestRepository,
            NgoRepository ngoRepository,
            FoodDonationRepository foodDonationRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            DonorRepository donorRepository,
            com.food_waste_backend.repository.PickupRequestRepository pickupRequestRepository,
            com.food_waste_backend.repository.VolunteerRepository volunteerRepository) {
        this.foodRequestRepository = foodRequestRepository;
        this.ngoRepository = ngoRepository;
        this.foodDonationRepository = foodDonationRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.donorRepository = donorRepository;
        this.pickupRequestRepository = pickupRequestRepository;
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    @Transactional
    public FoodRequestResponse createFoodRequest(String email, FoodRequestDto request) {
        FoodDonation donation = foodDonationRepository.findById(request.getDonationId())
                .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", request.getDonationId()));

        // Food Safety & Expiry Validation
        LocalDateTime now = LocalDateTime.now();
        if (donation.getExpiryTime().isBefore(now) || 
            (donation.getSafeUseDeadline() != null && donation.getSafeUseDeadline().isBefore(now))) {
            throw new IllegalArgumentException("Cannot request expired or past safe-use deadline food donation");
        }

        if (donation.getStatus() == DonationStatus.CANCELLED || 
            donation.getStatus() == DonationStatus.EXPIRED || 
            donation.getStatus() == DonationStatus.COMPLETED) {
            throw new IllegalStateException("Donation is no longer available (Status: " + donation.getStatus() + ")");
        }

        // Quantity Check & Prevention of Over-Allocation
        BigDecimal remaining = donation.getRemainingQuantity();
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Food donation has already been fully allocated");
        }

        BigDecimal reqQty = (request.getRequestedQuantity() != null && request.getRequestedQuantity().compareTo(BigDecimal.ZERO) > 0)
                ? request.getRequestedQuantity()
                : remaining;

        if (reqQty.compareTo(remaining) > 0) {
            throw new IllegalArgumentException("Requested quantity (" + reqQty + ") exceeds available remaining quantity (" + remaining + " " + donation.getQuantityUnit() + ")");
        }

        // NGO Lookup & Verification Enforcement
        Ngo ngo;
        if (email != null && !email.isBlank()) {
            ngo = ngoRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found for email: " + email));
        } else {
            ngo = ngoRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No NGO profile found"));
        }

        if (!Boolean.TRUE.equals(ngo.getVerified())) {
            throw new AccessDeniedException("Only verified NGOs can request food donations. Your organization is pending administrative verification.");
        }

        // Prevent Duplicate Active Requests from the Same NGO for this Donation
        List<FoodRequest> existingRequests = foodRequestRepository.findByDonationId(donation.getId());
        boolean hasDuplicate = existingRequests.stream()
                .anyMatch(r -> r.getNgo() != null && r.getNgo().getId().equals(ngo.getId()) &&
                        (r.getStatus() == RequestStatus.PENDING || r.getStatus() == RequestStatus.APPROVED));

        if (hasDuplicate) {
            throw new IllegalStateException("Your organization already has an active pending or approved request for this donation.");
        }

        FoodRequest foodRequest = new FoodRequest();
        foodRequest.setNgo(ngo);
        foodRequest.setDonation(donation);
        foodRequest.setRequestedQuantity(reqQty);
        foodRequest.setBeneficiaryCount(request.getBeneficiaryCount() != null ? request.getBeneficiaryCount() : 0);
        foodRequest.setDeliveryNotes(request.getDeliveryNotes());
        foodRequest.setNotes(request.getNotes());
        foodRequest.setStatus(RequestStatus.PENDING);

        FoodRequest savedRequest = foodRequestRepository.save(foodRequest);

        // Update status of donation to REQUESTED if still AVAILABLE
        if (donation.getStatus() == DonationStatus.AVAILABLE) {
            donation.setStatus(DonationStatus.REQUESTED);
            foodDonationRepository.save(donation);
        }

        // Notify Donor of Incoming Request
        if (donation.getDonor() != null && donation.getDonor().getUser() != null) {
            Notification notification = new Notification();
            notification.setUser(donation.getDonor().getUser());
            notification.setTitle("New Food Request");
            notification.setMessage(ngo.getNgoName() + " has requested " + reqQty + " " + donation.getQuantityUnit() + " of '" + donation.getTitle() + "'");
            notification.setType(NotificationType.REQUEST);
            notification.setIsRead(false);
            notificationRepository.save(notification);
        }

        return mapToResponse(savedRequest);
    }

    @Override
    public List<FoodRequestResponse> getAllFoodRequests() {
        return foodRequestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodRequestResponse> getMyFoodRequests(String email) {
        if (email != null && !email.isBlank()) {
            Ngo ngo = ngoRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found for email: " + email));

            return foodRequestRepository.findByNgoId(ngo.getId())
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        return foodRequestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodRequestResponse> getIncomingRequestsForDonor(String email) {
        if (email == null || email.isBlank()) {
            return foodRequestRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Donor donor = donorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found for user: " + email));

        return foodRequestRepository.findByDonorId(donor.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FoodRequestResponse approveFoodRequest(Long id, String email) {
        FoodRequest request = foodRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodRequest", id));

        // Authorization: Ensure caller owns the donation or is ADMIN
        if (email != null && !email.isBlank()) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null && user.getRole() != UserRole.ADMIN) {
                if (request.getDonation().getDonor() == null || 
                    request.getDonation().getDonor().getUser() == null ||
                    !request.getDonation().getDonor().getUser().getEmail().equals(email)) {
                    throw new AccessDeniedException("Unauthorized to approve requests for another donor's donation");
                }
            }
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved (Current status: " + request.getStatus() + ")");
        }

        FoodDonation donation = request.getDonation();
        BigDecimal allocated = donation.getAllocatedQuantity() != null ? donation.getAllocatedQuantity() : BigDecimal.ZERO;
        BigDecimal reqQty = request.getRequestedQuantity() != null ? request.getRequestedQuantity() : donation.getRemainingQuantity();

        donation.setAllocatedQuantity(allocated.add(reqQty));

        // If donation is now fully allocated, mark as ACCEPTED
        if (donation.getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            donation.setStatus(DonationStatus.ACCEPTED);
        }
        foodDonationRepository.save(donation);

        request.setStatus(RequestStatus.APPROVED);
        FoodRequest updated = foodRequestRepository.save(request);

        // Schedule a Collection/Pickup task for the approved request
        com.food_waste_backend.Entity.PickupRequest pickup = new com.food_waste_backend.Entity.PickupRequest();
        pickup.setDonation(donation);
        pickup.setFoodRequest(request);
        pickup.setStatus(com.food_waste_backend.enums.PickupStatus.SCHEDULED);
        pickup.setScheduledTime(LocalDateTime.now().plusHours(2));
        pickup.setPickupAddress(donation.getPickupAddress());
        pickup.setContactPhone(donation.getContactPhone());
        if (request.getDeliveryNotes() != null && !request.getDeliveryNotes().isBlank()) {
            pickup.setDeliveryAddress(request.getDeliveryNotes());
        } else if (request.getNgo() != null) {
            pickup.setDeliveryAddress(request.getNgo().getNgoName() + " Redistribution Center");
        }

        // Assign available volunteer if one exists
        List<com.food_waste_backend.Entity.Volunteer> availableVolunteers = volunteerRepository.findAll().stream()
                .filter(v -> Boolean.TRUE.equals(v.getAvailable()))
                .toList();
        if (!availableVolunteers.isEmpty()) {
            com.food_waste_backend.Entity.Volunteer assignedVolunteer = availableVolunteers.get(0);
            pickup.setVolunteer(assignedVolunteer);
            if (assignedVolunteer.getUser() != null) {
                Notification volunteerNotif = new Notification();
                volunteerNotif.setUser(assignedVolunteer.getUser());
                volunteerNotif.setTitle("New Food Collection Assigned");
                volunteerNotif.setMessage("You have been assigned to collect '" + donation.getTitle() + "' at " + (donation.getPickupAddress() != null ? donation.getPickupAddress() : "Donor address"));
                volunteerNotif.setType(NotificationType.PICKUP);
                volunteerNotif.setIsRead(false);
                notificationRepository.save(volunteerNotif);
            }
        }
        pickupRequestRepository.save(pickup);

        // Notify NGO about Approval
        if (request.getNgo() != null && request.getNgo().getUser() != null) {
            Notification notification = new Notification();
            notification.setUser(request.getNgo().getUser());
            notification.setTitle("Request Approved");
            notification.setMessage("Your food request for " + reqQty + " " + donation.getQuantityUnit() + " of '" + donation.getTitle() + "' has been approved by the donor!");
            notification.setType(NotificationType.REQUEST);
            notification.setIsRead(false);
            notificationRepository.save(notification);
        }

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public FoodRequestResponse approveFoodRequest(Long id) {
        return approveFoodRequest(id, null);
    }

    @Override
    @Transactional
    public FoodRequestResponse rejectFoodRequest(Long id, String email) {
        FoodRequest request = foodRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodRequest", id));

        if (email != null && !email.isBlank()) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null && user.getRole() != UserRole.ADMIN) {
                if (request.getDonation().getDonor() == null || 
                    request.getDonation().getDonor().getUser() == null ||
                    !request.getDonation().getDonor().getUser().getEmail().equals(email)) {
                    throw new AccessDeniedException("Unauthorized to reject requests for another donor's donation");
                }
            }
        }

        request.setStatus(RequestStatus.REJECTED);
        FoodRequest updated = foodRequestRepository.save(request);

        // Check if donation should be restored to AVAILABLE if no other requests remain
        FoodDonation donation = request.getDonation();
        if (donation != null) {
            List<FoodRequest> otherRequests = foodRequestRepository.findByDonationId(donation.getId());
            boolean hasOtherActive = otherRequests.stream()
                    .anyMatch(r -> !r.getId().equals(id) && 
                            (r.getStatus() == RequestStatus.PENDING || r.getStatus() == RequestStatus.APPROVED));
            if (!hasOtherActive && donation.getAllocatedQuantity().compareTo(BigDecimal.ZERO) == 0) {
                donation.setStatus(DonationStatus.AVAILABLE);
                foodDonationRepository.save(donation);
            }
        }

        // Notify NGO about Rejection
        if (request.getNgo() != null && request.getNgo().getUser() != null) {
            Notification notification = new Notification();
            notification.setUser(request.getNgo().getUser());
            notification.setTitle("Request Declined");
            notification.setMessage("Your food request for '" + request.getDonation().getTitle() + "' was declined by the donor.");
            notification.setType(NotificationType.REQUEST);
            notification.setIsRead(false);
            notificationRepository.save(notification);
        }

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public FoodRequestResponse rejectFoodRequest(Long id) {
        return rejectFoodRequest(id, null);
    }

    @Override
    @Transactional
    public FoodRequestResponse cancelFoodRequest(Long id, String email) {
        FoodRequest request = foodRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodRequest", id));

        if (email != null && !email.isBlank()) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null && user.getRole() != UserRole.ADMIN) {
                if (request.getNgo() == null || 
                    request.getNgo().getUser() == null ||
                    !request.getNgo().getUser().getEmail().equals(email)) {
                    throw new AccessDeniedException("Unauthorized to cancel this request");
                }
            }
        }

        // If previously approved, return quantity back to donation
        if (request.getStatus() == RequestStatus.APPROVED && request.getRequestedQuantity() != null) {
            FoodDonation donation = request.getDonation();
            BigDecimal allocated = donation.getAllocatedQuantity() != null ? donation.getAllocatedQuantity() : BigDecimal.ZERO;
            donation.setAllocatedQuantity(allocated.subtract(request.getRequestedQuantity()));
            if (donation.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0) {
                donation.setStatus(DonationStatus.AVAILABLE);
            }
            foodDonationRepository.save(donation);
        }

        request.setStatus(RequestStatus.CANCELLED);
        FoodRequest updated = foodRequestRepository.save(request);
        return mapToResponse(updated);
    }

    private FoodRequestResponse mapToResponse(FoodRequest request) {
        FoodRequestResponse response = new FoodRequestResponse();
        response.setId(request.getId());
        response.setNgoId(request.getNgo() != null ? request.getNgo().getId() : null);
        response.setNgoName(request.getNgo() != null ? request.getNgo().getNgoName() : "Community NGO");
        response.setStatus(request.getStatus() != null ? request.getStatus().name() : null);
        response.setRequestedQuantity(request.getRequestedQuantity());
        response.setBeneficiaryCount(request.getBeneficiaryCount());
        response.setDeliveryNotes(request.getDeliveryNotes());
        response.setNotes(request.getNotes());
        response.setRequestedAt(request.getRequestedAt());
        
        if (request.getDonation() != null) {
            response.setDonationId(request.getDonation().getId());
            response.setDonationTitle(request.getDonation().getTitle());
        }
        
        return response;
    }
}
