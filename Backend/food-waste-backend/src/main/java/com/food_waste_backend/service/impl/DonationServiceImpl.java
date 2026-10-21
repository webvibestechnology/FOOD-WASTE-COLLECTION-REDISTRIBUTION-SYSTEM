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
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.dto.DonationRequest;
import com.food_waste_backend.dto.DonationResponse;
import com.food_waste_backend.enums.DonationStatus;
import com.food_waste_backend.enums.FoodCategory;
import com.food_waste_backend.enums.UserRole;
import com.food_waste_backend.exception.ResourceNotFoundException;
import com.food_waste_backend.repository.DonorRepository;
import com.food_waste_backend.repository.FoodDonationRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.service.DonationService;

@Service
public class DonationServiceImpl implements DonationService {

    private final FoodDonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final UserRepository userRepository;

    public DonationServiceImpl(FoodDonationRepository donationRepository, 
                               DonorRepository donorRepository,
                               UserRepository userRepository) {
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DonationResponse createDonation(DonationRequest request, Long userId) {
        // Validate expiry time is in the future
        LocalDateTime now = LocalDateTime.now();
        if (request.getExpiryTime() != null && request.getExpiryTime().isBefore(now)) {
            throw new IllegalArgumentException("Expiry time must be in the future");
        }
        if (request.getSafeUseDeadline() != null && request.getSafeUseDeadline().isBefore(now)) {
            throw new IllegalArgumentException("Safe-use deadline must be in the future");
        }

        Donor donor;

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));

            donor = donorRepository.findByUserId(user.getId())
                    .orElseGet(() -> {
                        Donor newDonor = new Donor();
                        newDonor.setUser(user);
                        newDonor.setOrganizationName(user.getName());
                        newDonor.setCreatedAt(LocalDateTime.now());
                        return donorRepository.save(newDonor);
                    });
        } else {
            donor = donorRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No donor found"));
        }

        FoodDonation donation = new FoodDonation();
        donation.setDonor(donor);
        donation.setTitle(request.getTitle());
        donation.setDescription(request.getDescription());
        donation.setQuantity(BigDecimal.valueOf(request.getQuantity()));
        donation.setQuantityUnit(request.getQuantityUnit());
        donation.setAllocatedQuantity(BigDecimal.ZERO);
        donation.setCategory(FoodCategory.valueOf(request.getCategory()));
        donation.setExpiryTime(request.getExpiryTime());
        donation.setPreparationTime(request.getPreparationTime());
        donation.setSafeUseDeadline(request.getSafeUseDeadline() != null ? request.getSafeUseDeadline() : request.getExpiryTime());
        donation.setAllergens(request.getAllergens());
        donation.setStorageRequirements(request.getStorageRequirements());
        donation.setDietaryInfo(request.getDietaryInfo());
        donation.setPickupAddress(request.getPickupAddress());
        donation.setContactPhone(request.getContactPhone() != null ? request.getContactPhone() : 
                (donor.getUser() != null ? donor.getUser().getPhone() : null));
        donation.setStatus(DonationStatus.AVAILABLE);

        donation = donationRepository.save(donation);
        return mapToResponse(donation);
    }

    @Override
    @Transactional
    public DonationResponse createDonation(DonationRequest request, String email) {
        if (email != null && !email.isBlank()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
            return createDonation(request, user.getId());
        }
        return createDonation(request, (Long) null);
    }

    @Override
    public DonationResponse getDonationById(Long id) {
        FoodDonation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation", id));
        return mapToResponse(donation);
    }

    @Override
    public List<DonationResponse> getAllDonations() {
        return donationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DonationResponse> getDonationsByDonor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        return donorRepository.findByUserId(user.getId())
                .map(donor -> donationRepository.findByDonorId(donor.getId()).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()))
                .orElse(java.util.Collections.emptyList());
    }

    @Override
    public List<DonationResponse> getMyDonations(String email) {
        if (email == null || email.isBlank()) {
            return getAllDonations();
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
        return getDonationsByDonor(user.getId());
    }

    @Override
    @Transactional
    public DonationResponse updateDonationStatus(Long id, String status) {
        return updateDonationStatus(id, status, null);
    }

    @Override
    @Transactional
    public DonationResponse updateDonationStatus(Long id, String status, String email) {
        FoodDonation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation", id));

        // If email provided, verify user has permission (donor of the donation or admin)
        if (email != null && !email.isBlank()) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null && user.getRole() != UserRole.ADMIN) {
                if (donation.getDonor() == null || donation.getDonor().getUser() == null ||
                        !donation.getDonor().getUser().getEmail().equals(email)) {
                    throw new AccessDeniedException("Unauthorized to modify this donation");
                }
            }
        }

        donation.setStatus(DonationStatus.valueOf(status.toUpperCase()));
        donation = donationRepository.save(donation);
        return mapToResponse(donation);
    }

    @Override
    @Transactional
    public void deleteDonation(Long id) {
        deleteDonation(id, null);
    }

    @Override
    @Transactional
    public void deleteDonation(Long id, String email) {
        FoodDonation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation", id));

        if (email != null && !email.isBlank()) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null && user.getRole() != UserRole.ADMIN) {
                if (donation.getDonor() == null || donation.getDonor().getUser() == null ||
                        !donation.getDonor().getUser().getEmail().equals(email)) {
                    throw new AccessDeniedException("Unauthorized to delete this donation");
                }
            }
        }

        donationRepository.deleteById(id);
    }

    private DonationResponse mapToResponse(FoodDonation donation) {
        DonationResponse response = new DonationResponse();
        response.setId(donation.getId());
        response.setTitle(donation.getTitle());
        response.setDescription(donation.getDescription());
        response.setQuantity(donation.getQuantity() != null ? donation.getQuantity().intValue() : 0);
        response.setQuantityUnit(donation.getQuantityUnit());
        response.setAllocatedQuantity(donation.getAllocatedQuantity() != null ? donation.getAllocatedQuantity().intValue() : 0);
        response.setRemainingQuantity(donation.getRemainingQuantity() != null ? donation.getRemainingQuantity().intValue() : 0);
        response.setCategory(donation.getCategory() != null ? donation.getCategory().name() : null);
        response.setStatus(donation.getStatus() != null ? donation.getStatus().name() : null);
        response.setExpiryTime(donation.getExpiryTime());
        response.setPreparationTime(donation.getPreparationTime());
        response.setSafeUseDeadline(donation.getSafeUseDeadline());
        response.setAllergens(donation.getAllergens());
        response.setStorageRequirements(donation.getStorageRequirements());
        response.setDietaryInfo(donation.getDietaryInfo());
        response.setPickupAddress(donation.getPickupAddress());
        response.setContactPhone(donation.getContactPhone());
        response.setCreatedAt(donation.getCreatedAt());
        if (donation.getDonor() != null) {
            response.setDonorId(donation.getDonor().getId());
            response.setDonorName(donation.getDonor().getOrganizationName());
        }
        return response;
    }
}
