package com.food_waste_backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.food_waste_backend.Entity.User;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.exception.ResourceNotFoundException;
import com.food_waste_backend.repository.DonorRepository;
import com.food_waste_backend.repository.NgoRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.repository.VolunteerRepository;
import com.food_waste_backend.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final NgoRepository ngoRepository;
    private final VolunteerRepository volunteerRepository;

    public UserServiceImpl(UserRepository userRepository,
                           DonorRepository donorRepository,
                           NgoRepository ngoRepository,
                           VolunteerRepository volunteerRepository) {
        this.userRepository = userRepository;
        this.donorRepository = donorRepository;
        this.ngoRepository = ngoRepository;
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", id)
                );

        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUser(
            Long id,
            UserResponse userResponse) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", id)
                );

        user.setName(userResponse.getName());
        user.setPhone(userResponse.getPhone());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", id)
                );

        // Clean up linked profile records
        donorRepository.findByUserId(id).ifPresent(donorRepository::delete);
        ngoRepository.findByUserId(id).ifPresent(ngoRepository::delete);
        volunteerRepository.findByUserId(id).ifPresent(volunteerRepository::delete);

        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole() != null
                        ? user.getRole().name()
                        : null);
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
