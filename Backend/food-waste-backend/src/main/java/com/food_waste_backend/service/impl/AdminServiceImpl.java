package com.food_waste_backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.food_waste_backend.Entity.AuditLog;
import com.food_waste_backend.Entity.Ngo;
import com.food_waste_backend.dto.ReportResponse;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.exception.ResourceNotFoundException;
import com.food_waste_backend.repository.AuditLogRepository;
import com.food_waste_backend.repository.DonorRepository;
import com.food_waste_backend.repository.FoodDonationRepository;
import com.food_waste_backend.repository.FoodRequestRepository;
import com.food_waste_backend.repository.NgoRepository;
import com.food_waste_backend.repository.PickupRequestRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.repository.VolunteerRepository;
import com.food_waste_backend.service.AdminService;
import com.food_waste_backend.service.UserService;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final NgoRepository ngoRepository;
    private final VolunteerRepository volunteerRepository;
    private final FoodDonationRepository foodDonationRepository;
    private final FoodRequestRepository foodRequestRepository;
    private final PickupRequestRepository pickupRequestRepository;
    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminServiceImpl(
            UserService userService,
            NgoRepository ngoRepository,
            VolunteerRepository volunteerRepository,
            FoodDonationRepository foodDonationRepository,
            FoodRequestRepository foodRequestRepository,
            PickupRequestRepository pickupRequestRepository,
            UserRepository userRepository,
            DonorRepository donorRepository,
            AuditLogRepository auditLogRepository) {
        this.userService = userService;
        this.ngoRepository = ngoRepository;
        this.volunteerRepository = volunteerRepository;
        this.foodDonationRepository = foodDonationRepository;
        this.foodRequestRepository = foodRequestRepository;
        this.pickupRequestRepository = pickupRequestRepository;
        this.userRepository = userRepository;
        this.donorRepository = donorRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        deleteUser(id, "admin@system");
    }

    @Override
    @Transactional
    public void deleteUser(Long id, String adminEmail) {
        userService.deleteUser(id);
        auditLogRepository.save(new AuditLog(
                adminEmail != null ? adminEmail : "admin@system",
                "DELETE_USER",
                "USER",
                id != null ? id.toString() : "",
                "Permanently removed user account with ID: " + id
        ));
    }

    @Override
    @Transactional
    public void verifyNgo(Long id) {
        verifyNgo(id, "admin@system");
    }

    @Override
    @Transactional
    public void verifyNgo(Long id, String adminEmail) {
        Ngo ngo = ngoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NGO", id));
        ngo.setVerified(true);
        ngoRepository.save(ngo);

        auditLogRepository.save(new AuditLog(
                adminEmail != null ? adminEmail : "admin@system",
                "VERIFY_NGO",
                "NGO",
                id != null ? id.toString() : "",
                "Approved and verified NGO: " + ngo.getNgoName() + " (Reg: " + ngo.getRegistrationNumber() + ")"
        ));
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id) {
        toggleUserStatus(id, "admin@system");
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id, String adminEmail) {
        userRepository.findById(id).ifPresent(user -> {
            boolean newStatus = !Boolean.TRUE.equals(user.getEnabled());
            user.setEnabled(newStatus);
            userRepository.save(user);

            auditLogRepository.save(new AuditLog(
                    adminEmail != null ? adminEmail : "admin@system",
                    "TOGGLE_USER_STATUS",
                    "USER",
                    id != null ? id.toString() : "",
                    "Changed user " + user.getEmail() + " enabled status to: " + newStatus
            ));
        });
    }

    @Override
    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    public ReportResponse getReports() {
        ReportResponse response = new ReportResponse();

        long totalNgos = ngoRepository.count();
        long verifiedNgos = ngoRepository.findByVerifiedTrue().size();
        long activeVolunteers = volunteerRepository.findByAvailableTrue().size();
        long totalDonations = foodDonationRepository.count();
        long totalRequests = foodRequestRepository.count();
        long totalPickups = pickupRequestRepository.count();
        long totalUsers = userRepository.count();
        long totalDonors = donorRepository.count();

        response.setTotalDonations(totalDonations);
        response.setTotalPickups(totalPickups);
        response.setTotalRequests(totalRequests);
        response.setActiveVolunteers(activeVolunteers);
        response.setActiveNgos(verifiedNgos);
        response.setTotalUsers(totalUsers);
        response.setTotalDonors(totalDonors);
        response.setTotalNgos(totalNgos);

        return response;
    }
}
