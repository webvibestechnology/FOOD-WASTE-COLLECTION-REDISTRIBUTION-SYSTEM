package com.food_waste_backend.service;

import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.Entity.Donor;

public interface DonorService {

    UserResponse registerAsDonor(
            Long userId,
            Donor donor
    );

    Donor getDonorProfile(Long userId);

    Donor updateDonorProfile(
            Long userId,
            Donor donor
    );
}