package com.food_waste_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationRequest {

    private String title;
    private String description;
    private Integer quantity;
    private String quantityUnit;
    private String category;
    private LocalDateTime expiryTime;
}