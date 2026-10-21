package com.food_waste_backend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food_waste_backend.dto.ReportResponse;
import com.food_waste_backend.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DONOR', 'NGO')")
    public ResponseEntity<ReportResponse> getReports() {
        return ResponseEntity.ok(reportService.getReports());
    }

    @GetMapping("/stats")
    public ResponseEntity<ReportResponse> getStats() {
        return ResponseEntity.ok(reportService.getReports());
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'DONOR', 'NGO')")
    public ResponseEntity<byte[]> exportCsv() {
        String csv = reportService.generateCsvReport();
        byte[] bytes = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"food_waste_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
