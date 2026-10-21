package com.food_waste_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food_waste_backend.Entity.Donor;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.exception.ResourceNotFoundException;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.service.DonorService;

@RestController
@RequestMapping("/api/donors")
@CrossOrigin(origins = "http://localhost:4200")
public class DonorController {

    private final DonorService donorService;
    private final UserRepository userRepository;

    public DonorController(DonorService donorService, UserRepository userRepository) {
        this.donorService = donorService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerAsDonor(
            Authentication authentication,
            @RequestBody Donor donor) {

        String email = authentication.getName();

        Long userId = getUserIdFromAuthentication(email);

        UserResponse response =
                donorService.registerAsDonor(userId, donor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<Donor> getDonorProfile(
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = getUserIdFromAuthentication(email);

        return ResponseEntity.ok(
                donorService.getDonorProfile(userId)
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<Donor> updateDonorProfile(
            Authentication authentication,
            @RequestBody Donor donor) {

        String email = authentication.getName();

        Long userId = getUserIdFromAuthentication(email);

        return ResponseEntity.ok(
                donorService.updateDonorProfile(
                        userId,
                        donor
                )
        );
    }

    private Long getUserIdFromAuthentication(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return user.getId();
    }
}