package com.food_waste_backend.service;

import java.util.List;

import com.food_waste_backend.dto.DonationRequest;
import com.food_waste_backend.dto.DonationResponse;

public interface DonationService {

    // Create new donation with userId or email
    DonationResponse createDonation(
            DonationRequest request,
            Long userId
    );

    DonationResponse createDonation(
            DonationRequest request,
            String email
    );

    // Get donation by ID
    DonationResponse getDonationById(Long id);

    // Get all donations
    List<DonationResponse> getAllDonations();

    // Get donations of a particular donor
    List<DonationResponse> getDonationsByDonor(Long userId);

    List<DonationResponse> getMyDonations(String email);

    // Update donation status
    DonationResponse updateDonationStatus(
            Long id,
            String status
    );

    DonationResponse updateDonationStatus(
            Long id,
            String status,
            String email
    );

    // Delete donation
    void deleteDonation(Long id);

    void deleteDonation(Long id, String email);
}
