package com.food_waste_backend.service;

import java.util.List;

import com.food_waste_backend.dto.NgoRequest;
import com.food_waste_backend.dto.NgoResponse;
import com.food_waste_backend.dto.UserResponse;

public interface NgoService {

    UserResponse registerAsNgo(String email, NgoRequest request);

    NgoResponse getNgoProfile(String email);

    NgoResponse updateNgoProfile(String email, NgoRequest request);

    List<NgoResponse> getAllNgos();

    List<NgoResponse> getVerifiedNgos();
}
