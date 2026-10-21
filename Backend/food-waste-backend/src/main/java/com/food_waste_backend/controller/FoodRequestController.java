package com.food_waste_backend.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food_waste_backend.dto.FoodRequestDto;
import com.food_waste_backend.dto.FoodRequestResponse;
import com.food_waste_backend.service.FoodRequestService;

@RestController
@RequestMapping("/api/food-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class FoodRequestController {

    private final FoodRequestService foodRequestService;

    public FoodRequestController(FoodRequestService foodRequestService) {
        this.foodRequestService = foodRequestService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Food Request Controller is working!");
    }

    @PostMapping
    public ResponseEntity<FoodRequestResponse> createFoodRequest(
            @Valid @RequestBody FoodRequestDto request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        FoodRequestResponse response = foodRequestService.createFoodRequest(email, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FoodRequestResponse>> getAllFoodRequests() {
        return ResponseEntity.ok(foodRequestService.getAllFoodRequests());
    }

    @GetMapping("/my")
    public ResponseEntity<List<FoodRequestResponse>> getMyFoodRequests(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(foodRequestService.getMyFoodRequests(email));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<FoodRequestResponse>> getIncomingRequests(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(foodRequestService.getIncomingRequestsForDonor(email));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<FoodRequestResponse> approveFoodRequest(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(foodRequestService.approveFoodRequest(id, email));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<FoodRequestResponse> rejectFoodRequest(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(foodRequestService.rejectFoodRequest(id, email));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<FoodRequestResponse> cancelFoodRequest(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(foodRequestService.cancelFoodRequest(id, email));
    }
}
