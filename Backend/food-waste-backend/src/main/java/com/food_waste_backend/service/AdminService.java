package com.food_waste_backend.service;

import java.util.List;

import com.food_waste_backend.Entity.AuditLog;
import com.food_waste_backend.dto.ReportResponse;
import com.food_waste_backend.dto.UserResponse;

public interface AdminService {

    List<UserResponse> getAllUsers();

    void deleteUser(Long id);

    void deleteUser(Long id, String adminEmail);

    void verifyNgo(Long id);

    void verifyNgo(Long id, String adminEmail);

    void toggleUserStatus(Long id);

    void toggleUserStatus(Long id, String adminEmail);

    ReportResponse getReports();

    List<AuditLog> getAuditLogs();
}
