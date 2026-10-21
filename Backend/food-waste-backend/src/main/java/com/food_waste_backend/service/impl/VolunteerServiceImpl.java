package com.food_waste_backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.food_waste_backend.Entity.User;
import com.food_waste_backend.Entity.Volunteer;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.dto.VolunteerRequest;
import com.food_waste_backend.dto.VolunteerResponse;
import com.food_waste_backend.exception.ResourceNotFoundException;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.repository.VolunteerRepository;
import com.food_waste_backend.service.VolunteerService;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;

    public VolunteerServiceImpl(
            VolunteerRepository volunteerRepository,
            UserRepository userRepository) {
        this.volunteerRepository = volunteerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse registerAsVolunteer(String email, VolunteerRequest request) {
        User user;
        if (email != null) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        } else {
            // Create a default user for demo purposes
            user = new User();
            user.setName("Volunteer User");
            user.setEmail("volunteer@example.com");
            user.setPhone("5555555555");
            user.setRole(com.food_waste_backend.enums.UserRole.VOLUNTEER);
            user.setEnabled(true);
            user.setCreatedAt(java.time.LocalDateTime.now());
            user.setUpdatedAt(java.time.LocalDateTime.now());
            user = userRepository.save(user);
        }
        
        if (volunteerRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User is already registered as a volunteer");
        }
        
        Volunteer volunteer = new Volunteer();
        volunteer.setUser(user);
        volunteer.setVehicleType(request.getVehicleType());
        volunteer.setOperatingArea(request.getOperatingArea());
        
        volunteerRepository.save(volunteer);
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole() != null ? user.getRole().name() : null);
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    @Override
    public VolunteerResponse getVolunteerProfile(String email) {
        Volunteer volunteer;
        if (email != null) {
            volunteer = volunteerRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for email: " + email));
        } else {
            volunteer = volunteerRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No volunteer profiles found"));
        }
        
        return mapToResponse(volunteer);
    }

    @Override
    public VolunteerResponse updateVolunteerProfile(String email, VolunteerRequest request) {
        Volunteer volunteer;
        if (email != null) {
            volunteer = volunteerRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for email: " + email));
        } else {
            volunteer = volunteerRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No volunteer profiles found"));
        }
        
        volunteer.setVehicleType(request.getVehicleType());
        volunteer.setOperatingArea(request.getOperatingArea());
        
        Volunteer updated = volunteerRepository.save(volunteer);
        return mapToResponse(updated);
    }

    @Override
    public VolunteerResponse updateAvailability(String email, Boolean available) {
        Volunteer volunteer;
        if (email != null) {
            volunteer = volunteerRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for email: " + email));
        } else {
            volunteer = volunteerRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No volunteer profiles found"));
        }

        if (available != null) {
            volunteer.setAvailable(available);
        }

        Volunteer updated = volunteerRepository.save(volunteer);
        return mapToResponse(updated);
    }

    @Override
    public List<VolunteerResponse> getAvailableVolunteers() {
        return volunteerRepository.findByAvailableTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private VolunteerResponse mapToResponse(Volunteer volunteer) {
        VolunteerResponse response = new VolunteerResponse();
        response.setId(volunteer.getId());
        response.setVolunteerName(volunteer.getUser() != null ? volunteer.getUser().getName() : null);
        response.setAvailable(volunteer.getAvailable() != null ? volunteer.getAvailable() : false);
        response.setVehicleType(volunteer.getVehicleType());
        response.setOperatingArea(volunteer.getOperatingArea());
        return response;
    }
}
