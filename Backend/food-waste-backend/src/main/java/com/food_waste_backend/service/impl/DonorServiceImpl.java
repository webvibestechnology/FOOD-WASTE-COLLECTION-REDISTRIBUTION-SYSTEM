package com.food_waste_backend.service.impl;

import org.springframework.stereotype.Service;

import com.food_waste_backend.Entity.Donor;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.enums.UserRole;
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

        if (donor.getDonorType() == null) {
            donor.setDonorType(UserRole.DONOR);
        }

        Donor savedDonor = donorRepository.save(donor);

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null
                        ? user.getRole().name()
                        : null)
                .createdAt(user.getCreatedAt())
                .build();
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

        existingDonor.setDonorType(
                donor.getDonorType()
        );

        existingDonor.setDescription(
                donor.getDescription()
        );

        return donorRepository.save(existingDonor);
    }
}