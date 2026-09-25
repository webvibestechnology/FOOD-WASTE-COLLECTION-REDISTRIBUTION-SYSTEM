package com.food_waste_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NgoRequest {

    private String ngoName;
    private String registrationNumber;
    private String description;
    private String contactPerson;
}