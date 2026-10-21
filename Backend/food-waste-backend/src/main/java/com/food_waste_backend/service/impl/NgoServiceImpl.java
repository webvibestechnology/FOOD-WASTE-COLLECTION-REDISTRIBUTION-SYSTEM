package com.food_waste_backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.food_waste_backend.Entity.Ngo;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.dto.NgoRequest;
import com.food_waste_backend.dto.NgoResponse;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.enums.UserRole;
import com.food_waste_backend.exception.ResourceNotFoundException;
import com.food_waste_backend.repository.NgoRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.service.NgoService;

@Service
public class NgoServiceImpl implements NgoService {

    private final NgoRepository ngoRepository;
    private final UserRepository userRepository;

    public NgoServiceImpl(NgoRepository ngoRepository, UserRepository userRepository) {
        this.ngoRepository = ngoRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse registerAsNgo(String email, NgoRequest request) {
        User user;
        if (email != null) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
        } else {
            user = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == UserRole.NGO)
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No NGO user found"));
        }

        user.setRole(UserRole.NGO);
        userRepository.save(user);

        Ngo ngo = ngoRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Ngo newNgo = new Ngo();
                    newNgo.setUser(user);
                    return newNgo;
                });

        ngo.setNgoName(request.getNgoName());
        ngo.setRegistrationNumber(request.getRegistrationNumber());
        ngo.setDescription(request.getDescription());
        ngo.setContactPerson(request.getContactPerson());
        ngo.setRegisteredAt(LocalDateTime.now());
        ngo.setVerified(false); // Requires admin approval

        ngoRepository.save(ngo);

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    @Override
    public NgoResponse getNgoProfile(String email) {
        Ngo ngo;
        if (email != null) {
            ngo = ngoRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found for email: " + email));
        } else {
            ngo = ngoRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No NGO profiles found"));
        }

        return mapToResponse(ngo);
    }

    @Override
    public NgoResponse updateNgoProfile(String email, NgoRequest request) {
        Ngo ngo;
        if (email != null) {
            ngo = ngoRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found for email: " + email));
        } else {
            ngo = ngoRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No NGO profiles found"));
        }

        ngo.setNgoName(request.getNgoName());
        ngo.setRegistrationNumber(request.getRegistrationNumber());
        ngo.setDescription(request.getDescription());
        ngo.setContactPerson(request.getContactPerson());

        Ngo updated = ngoRepository.save(ngo);
        return mapToResponse(updated);
    }

    @Override
    public List<NgoResponse> getAllNgos() {
        return ngoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NgoResponse> getVerifiedNgos() {
        return ngoRepository.findByVerifiedTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NgoResponse mapToResponse(Ngo ngo) {
        NgoResponse response = new NgoResponse();
        response.setId(ngo.getId());
        response.setNgoName(ngo.getNgoName());
        response.setOrganizationName(ngo.getNgoName());
        response.setRegistrationNumber(ngo.getRegistrationNumber());
        response.setDescription(ngo.getDescription());
        response.setVerified(ngo.getVerified() != null ? ngo.getVerified() : false);
        response.setContactPerson(ngo.getContactPerson());
        response.setRegisteredAt(ngo.getRegisteredAt());
        if (ngo.getUser() != null) {
            response.setPhone(ngo.getUser().getPhone());
        }
        return response;
    }
}
