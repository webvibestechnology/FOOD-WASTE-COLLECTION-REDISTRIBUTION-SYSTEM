package com.food_waste_backend.service;

import java.util.List;

import com.food_waste_backend.dto.FoodResponse;

public interface FoodService {

    List<FoodResponse> getFoodByDonationId(Long donationId);
}
