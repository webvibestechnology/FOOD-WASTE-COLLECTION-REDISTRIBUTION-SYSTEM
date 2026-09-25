
package com.food_waste_backend.service;

import java.util.List;

import com.food_waste_backend.dto.DonationRequest;
import com.food_waste_backend.dto.DonationResponse;

public interface DonationService {

    // Create new donation
    DonationResponse createDonation(
            DonationRequest request,
            Long userId
    );

    // Get donation by ID
    DonationResponse getDonationById(Long id);

    // Get all donations
    List<DonationResponse> getAllDonations();

    // Get donations of a particular donor
    List<DonationResponse> getDonationsByDonor(Long userId);

    // Update donation status
    DonationResponse updateDonationStatus(
            Long id,
            String status
    );

    // Delete donation
    void deleteDonation(Long id);
}

