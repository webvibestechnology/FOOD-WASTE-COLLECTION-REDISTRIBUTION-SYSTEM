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
public class NgoResponse {

    private Long id;
    private String ngoName;
    private String registrationNumber;
    private boolean verified;
    private String contactPerson;
    private LocalDateTime registeredAt;
}