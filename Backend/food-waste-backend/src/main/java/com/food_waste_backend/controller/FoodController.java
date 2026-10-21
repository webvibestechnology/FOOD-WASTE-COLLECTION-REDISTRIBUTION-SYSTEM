package com.food_waste_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.food_waste_backend.dto.FoodResponse;
import com.food_waste_backend.service.FoodService;

@RestController
@RequestMapping("/api/food")
@CrossOrigin(origins = "http://localhost:4200")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ResponseEntity<List<FoodResponse>> getFoodByDonationId(
            @RequestParam Long donationId) {
        return ResponseEntity.ok(foodService.getFoodByDonationId(donationId));
    }
}
