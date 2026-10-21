package com.food_waste_backend.service;

import java.util.List;

import com.food_waste_backend.dto.PickupRequestDto;
import com.food_waste_backend.dto.PickupResponse;

public interface PickupService {

    PickupResponse createPickup(String email, PickupRequestDto request);

    List<PickupResponse> getAllPickups();

    List<PickupResponse> getMyPickups(String email);

    List<PickupResponse> getAvailablePickups();

    PickupResponse getPickupById(Long id);

    PickupResponse updatePickupStatus(Long id, String status);

    PickupResponse updatePickupStatus(Long id, String status, String email, String notes);

    PickupResponse claimPickup(Long id, String email);

    PickupResponse assignVolunteer(Long id, Long volunteerId);
}
