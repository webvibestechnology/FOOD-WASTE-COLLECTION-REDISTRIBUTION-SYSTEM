package com.food_waste_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {

    private Long totalDonations;
    private Long totalPickups;
    private Long totalRequests;
    private Long activeVolunteers;
    private Long activeNgos;
}