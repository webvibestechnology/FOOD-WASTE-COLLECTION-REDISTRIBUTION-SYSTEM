package com.food_waste_backend.service;

import java.util.List;

import com.food_waste_backend.dto.UserResponse;

public interface UserService {

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserResponse userResponse);

    void deleteUser(Long id);
}