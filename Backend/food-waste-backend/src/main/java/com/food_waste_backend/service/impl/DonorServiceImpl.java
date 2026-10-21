package com.food_waste_backend.service.impl;

import org.springframework.stereotype.Service;

import com.food_waste_backend.Entity.Donor;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.exception.BadRequestException;
import com.food_waste_backend.exception.ResourceNotFoundException;
import com.food_waste_backend.repository.DonorRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.service.DonorService;

@Service
public class DonorServiceImpl implements DonorService {

    private final DonorRepository donorRepository;
    private final UserRepository userRepository;

    public DonorServiceImpl(
            DonorRepository donorRepository,
            UserRepository userRepository) {

        this.donorRepository = donorRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse registerAsDonor(
            Long userId,
            Donor donor) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", userId)
                );

        if (donorRepository.findByUserId(userId).isPresent()) {
            throw new BadRequestException(
                    "User is already registered as a donor"
            );
        }

        donor.setUser(user);

        Donor savedDonor = donorRepository.save(donor);

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

    @Override
    public Donor getDonorProfile(Long userId) {

        return donorRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Donor profile for user",
                                userId
                        )
                );
    }

    @Override
    public Donor updateDonorProfile(
            Long userId,
            Donor donor) {

        Donor existingDonor = donorRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Donor profile for user",
                                userId
                        )
                );

        existingDonor.setOrganizationName(
                donor.getOrganizationName()
        );

        existingDonor.setDescription(
                donor.getDescription()
        );

        return donorRepository.save(existingDonor);
    }
}