package com.food_waste_backend.service;

import java.util.List;

import com.food_waste_backend.dto.FoodRequestDto;
import com.food_waste_backend.dto.FoodRequestResponse;

public interface FoodRequestService {

    FoodRequestResponse createFoodRequest(String email, FoodRequestDto request);

    List<FoodRequestResponse> getAllFoodRequests();

    List<FoodRequestResponse> getMyFoodRequests(String email);

    List<FoodRequestResponse> getIncomingRequestsForDonor(String email);

    FoodRequestResponse approveFoodRequest(Long id, String email);

    FoodRequestResponse rejectFoodRequest(Long id, String email);

    FoodRequestResponse cancelFoodRequest(Long id, String email);

    FoodRequestResponse approveFoodRequest(Long id);

    FoodRequestResponse rejectFoodRequest(Long id);
}
