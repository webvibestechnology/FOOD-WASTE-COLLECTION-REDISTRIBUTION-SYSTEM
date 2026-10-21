package com.food_waste_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.food_waste_backend.dto.PickupRequestDto;
import com.food_waste_backend.dto.PickupResponse;
import com.food_waste_backend.service.PickupService;

@RestController
@RequestMapping("/api/pickups")
@CrossOrigin(origins = "http://localhost:4200")
public class PickupController {

    private final PickupService pickupService;

    public PickupController(PickupService pickupService) {
        this.pickupService = pickupService;
    }

    @PostMapping
    public ResponseEntity<PickupResponse> createPickup(
            @RequestBody PickupRequestDto request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(pickupService.createPickup(email, request));
    }

    @GetMapping
    public ResponseEntity<List<PickupResponse>> getAllPickups() {
        return ResponseEntity.ok(pickupService.getAllPickups());
    }

    @GetMapping("/my")
    public ResponseEntity<List<PickupResponse>> getMyPickups(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(pickupService.getMyPickups(email));
    }

    @GetMapping("/available")
    public ResponseEntity<List<PickupResponse>> getAvailablePickups() {
        return ResponseEntity.ok(pickupService.getAvailablePickups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PickupResponse> getPickupById(@PathVariable Long id) {
        return ResponseEntity.ok(pickupService.getPickupById(id));
    }

    // Support both PATCH and PUT, and accept status via query param or body
    @RequestMapping(value = "/{id}/status", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<PickupResponse> updatePickupStatus(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String notes,
            @RequestBody(required = false) Map<String, String> body,
            Authentication authentication) {

        String effectiveStatus = status;
        String effectiveNotes = notes;

        if (body != null) {
            if (body.containsKey("status") && (effectiveStatus == null || effectiveStatus.isBlank())) {
                effectiveStatus = body.get("status");
            }
            if (body.containsKey("notes") && (effectiveNotes == null || effectiveNotes.isBlank())) {
                effectiveNotes = body.get("notes");
            }
        }

        if (effectiveStatus == null || effectiveStatus.isBlank()) {
            throw new IllegalArgumentException("Status must be provided");
        }

        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(pickupService.updatePickupStatus(id, effectiveStatus, email, effectiveNotes));
    }

    @PutMapping("/{id}/claim")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<PickupResponse> claimPickup(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(pickupService.claimPickup(id, email));
    }

    @RequestMapping(value = "/{id}/assign/{volunteerId}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    @PreAuthorize("hasAnyRole('ADMIN', 'DONOR')")
    public ResponseEntity<PickupResponse> assignVolunteer(
            @PathVariable Long id,
            @PathVariable Long volunteerId) {
        return ResponseEntity.ok(pickupService.assignVolunteer(id, volunteerId));
    }
}
