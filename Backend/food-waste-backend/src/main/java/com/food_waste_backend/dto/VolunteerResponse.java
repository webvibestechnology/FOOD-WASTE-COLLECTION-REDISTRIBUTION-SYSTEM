package com.food_waste_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerResponse {

    private Long id;
    private String volunteerName;
    private boolean available;
    private String vehicleType;
    private String operatingArea;
}