package com.food_waste_backend.service;

import com.food_waste_backend.dto.ReportResponse;

public interface ReportService {

    ReportResponse getReports();

    String generateCsvReport();
}
