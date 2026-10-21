package com.food_waste_backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food_waste_backend.Entity.Volunteer;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.dto.VolunteerRequest;
import com.food_waste_backend.dto.VolunteerResponse;
import com.food_waste_backend.service.VolunteerService;
import com.food_waste_backend.repository.VolunteerRepository;

@RestController
@RequestMapping("/api/volunteer")
@CrossOrigin(origins = "http://localhost:4200")
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final VolunteerRepository volunteerRepository;

    public VolunteerController(
            VolunteerService volunteerService,
            VolunteerRepository volunteerRepository) {
        this.volunteerService = volunteerService;
        this.volunteerRepository = volunteerRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerAsVolunteer(
            @RequestBody VolunteerRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(volunteerService.registerAsVolunteer(email, request));
    }

    @GetMapping("/profile")
    public ResponseEntity<VolunteerResponse> getVolunteerProfile(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(volunteerService.getVolunteerProfile(email));
    }

    @PutMapping("/profile")
    public ResponseEntity<VolunteerResponse> updateVolunteerProfile(
            @RequestBody VolunteerRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(volunteerService.updateVolunteerProfile(email, request));
    }

    @PutMapping("/availability")
    public ResponseEntity<VolunteerResponse> updateAvailability(
            @RequestBody(required = false) Boolean available,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(volunteerService.updateAvailability(email, available));
    }

    @GetMapping("/available")
    public ResponseEntity<List<VolunteerResponse>> getAvailableVolunteers() {
        return ResponseEntity.ok(volunteerService.getAvailableVolunteers());
    }

    @GetMapping
    public ResponseEntity<List<VolunteerResponse>> getAllVolunteers() {
        List<Volunteer> volunteers = volunteerRepository.findAll();
        List<VolunteerResponse> responses = volunteers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private VolunteerResponse mapToResponse(Volunteer volunteer) {
        VolunteerResponse response = new VolunteerResponse();
        response.setId(volunteer.getId());
        response.setVolunteerName(volunteer.getUser() != null ? volunteer.getUser().getName() : null);
        response.setEmail(volunteer.getUser() != null ? volunteer.getUser().getEmail() : null);
        response.setPhone(volunteer.getUser() != null ? volunteer.getUser().getPhone() : null);
        response.setVehicleType(volunteer.getVehicleType());
        response.setOperatingArea(volunteer.getOperatingArea());
        response.setAvailable(volunteer.getAvailable());
        return response;
    }
}
