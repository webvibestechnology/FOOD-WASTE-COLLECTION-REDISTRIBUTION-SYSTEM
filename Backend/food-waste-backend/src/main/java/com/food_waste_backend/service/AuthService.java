package com.food_waste_backend.service;

import com.food_waste_backend.dto.LoginRequest;
import com.food_waste_backend.dto.LoginResponse;
import com.food_waste_backend.dto.RegisterRequest;
import com.food_waste_backend.dto.UserResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    UserResponse register(RegisterRequest registerRequest);
}