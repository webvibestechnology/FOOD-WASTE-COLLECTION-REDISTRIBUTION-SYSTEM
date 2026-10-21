package com.food_waste_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food_waste_backend.Entity.AuditLog;
import com.food_waste_backend.dto.DonationResponse;
import com.food_waste_backend.dto.PickupResponse;
import com.food_waste_backend.dto.ReportResponse;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.service.AdminService;
import com.food_waste_backend.service.DonationService;
import com.food_waste_backend.service.PickupService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final AdminService adminService;
    private final DonationService donationService;
    private final PickupService pickupService;

    public AdminController(
            AdminService adminService,
            DonationService donationService,
            PickupService pickupService) {
        this.adminService = adminService;
        this.donationService = donationService;
        this.pickupService = pickupService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
        String adminEmail = authentication != null ? authentication.getName() : "admin@system";
        adminService.deleteUser(id, adminEmail);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/ngos/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verifyNgo(@PathVariable Long id, Authentication authentication) {
        String adminEmail = authentication != null ? authentication.getName() : "admin@system";
        adminService.verifyNgo(id, adminEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> getReports() {
        return ResponseEntity.ok(adminService.getReports());
    }

    @GetMapping("/donations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DonationResponse>> getAllDonations() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }

    @GetMapping("/pickups")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PickupResponse>> getAllPickups() {
        return ResponseEntity.ok(pickupService.getAllPickups());
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> getDashboardStats() {
        return ResponseEntity.ok(adminService.getReports());
    }

    @PutMapping("/users/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleUserStatus(@PathVariable Long id, Authentication authentication) {
        String adminEmail = authentication != null ? authentication.getName() : "admin@system";
        adminService.toggleUserStatus(id, adminEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(adminService.getAuditLogs());
    }
}
