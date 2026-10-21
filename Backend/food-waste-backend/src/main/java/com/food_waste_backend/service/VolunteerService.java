package com.food_waste_backend.service;

import java.util.List;

import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.dto.VolunteerRequest;
import com.food_waste_backend.dto.VolunteerResponse;

public interface VolunteerService {

    UserResponse registerAsVolunteer(String email, VolunteerRequest request);

    VolunteerResponse getVolunteerProfile(String email);

    VolunteerResponse updateVolunteerProfile(String email, VolunteerRequest request);

    VolunteerResponse updateAvailability(String email, Boolean available);

    List<VolunteerResponse> getAvailableVolunteers();
}
