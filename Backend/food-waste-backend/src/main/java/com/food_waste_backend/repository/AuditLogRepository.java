package com.food_waste_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.food_waste_backend.Entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByTimestampDesc();
}
