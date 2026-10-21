package com.food_waste_backend.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.food_waste_backend.dto.DonationRequest;
import com.food_waste_backend.dto.DonationResponse;
import com.food_waste_backend.service.DonationService;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "http://localhost:4200")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    public ResponseEntity<DonationResponse> createDonation(
            @Valid @RequestBody DonationRequest request,
            Authentication authentication
    ) {
        String email = authentication != null ? authentication.getName() : null;
        DonationResponse response = donationService.createDonation(request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationResponse> getDonationById(@PathVariable Long id) {
        DonationResponse response = donationService.getDonationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DonationResponse>> getAllDonations() {
        List<DonationResponse> donations = donationService.getAllDonations();
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/my")
    public ResponseEntity<List<DonationResponse>> getMyDonations(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        List<DonationResponse> donations = donationService.getMyDonations(email);
        return ResponseEntity.ok(donations);
    }

    // Support both PUT and PATCH for status update to avoid HTTP 405 Method Not Allowed
    @RequestMapping(value = "/{id}/status", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<DonationResponse> updateDonationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication
    ) {
        String email = authentication != null ? authentication.getName() : null;
        DonationResponse response = donationService.updateDonationStatus(id, status, email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        donationService.deleteDonation(id, email);
        return ResponseEntity.noContent().build();
    }
}
