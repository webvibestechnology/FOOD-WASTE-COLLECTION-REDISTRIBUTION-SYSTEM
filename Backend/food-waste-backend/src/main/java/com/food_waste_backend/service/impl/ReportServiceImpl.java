package com.food_waste_backend.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.food_waste_backend.Entity.FoodDonation;
import com.food_waste_backend.dto.ReportResponse;
import com.food_waste_backend.repository.DonorRepository;
import com.food_waste_backend.repository.FoodDonationRepository;
import com.food_waste_backend.repository.FoodRequestRepository;
import com.food_waste_backend.repository.NgoRepository;
import com.food_waste_backend.repository.PickupRequestRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.repository.VolunteerRepository;
import com.food_waste_backend.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

    private final FoodDonationRepository foodDonationRepository;
    private final FoodRequestRepository foodRequestRepository;
    private final PickupRequestRepository pickupRequestRepository;
    private final VolunteerRepository volunteerRepository;
    private final NgoRepository ngoRepository;
    private final UserRepository userRepository;
    private final DonorRepository donorRepository;

    public ReportServiceImpl(
            FoodDonationRepository foodDonationRepository,
            FoodRequestRepository foodRequestRepository,
            PickupRequestRepository pickupRequestRepository,
            VolunteerRepository volunteerRepository,
            NgoRepository ngoRepository,
            UserRepository userRepository,
            DonorRepository donorRepository) {
        this.foodDonationRepository = foodDonationRepository;
        this.foodRequestRepository = foodRequestRepository;
        this.pickupRequestRepository = pickupRequestRepository;
        this.volunteerRepository = volunteerRepository;
        this.ngoRepository = ngoRepository;
        this.userRepository = userRepository;
        this.donorRepository = donorRepository;
    }

    @Override
    public ReportResponse getReports() {
        ReportResponse response = new ReportResponse();

        long totalDonations = foodDonationRepository.count();
        long totalPickups = pickupRequestRepository.count();
        long totalRequests = foodRequestRepository.count();
        long activeVolunteers = volunteerRepository.findByAvailableTrue().size();
        long verifiedNgos = ngoRepository.findByVerifiedTrue().size();
        long totalNgos = ngoRepository.count();
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

    @Override
    public String generateCsvReport() {
        StringBuilder csv = new StringBuilder();

        // 1. Header & Platform Metrics
        csv.append("FOOD WASTE COLLECTION & REDISTRIBUTION PLATFORM REPORT\n");
        csv.append("Generated At,").append(java.time.LocalDateTime.now()).append("\n\n");

        csv.append("METRIC,COUNT\n");
        csv.append("Total Food Listings,").append(foodDonationRepository.count()).append("\n");
        csv.append("Total Redistribution Requests,").append(foodRequestRepository.count()).append("\n");
        csv.append("Total Collections & Deliveries,").append(pickupRequestRepository.count()).append("\n");
        csv.append("Verified NGO Partners,").append(ngoRepository.findByVerifiedTrue().size()).append("\n");
        csv.append("Total Registered NGOs,").append(ngoRepository.count()).append("\n");
        csv.append("Active Volunteers Available,").append(volunteerRepository.findByAvailableTrue().size()).append("\n");
        csv.append("Total Food Donors,").append(donorRepository.count()).append("\n");
        csv.append("Total Platform Users,").append(userRepository.count()).append("\n\n");

        // 2. Donation Categories Breakdown
        csv.append("DONATION BREAKDOWN BY CATEGORY\n");
        csv.append("CATEGORY,COUNT\n");
        List<FoodDonation> allDonations = foodDonationRepository.findAll();
        Map<String, Long> byCategory = allDonations.stream()
                .collect(Collectors.groupingBy(d -> d.getCategory() != null ? d.getCategory().name() : "OTHER", Collectors.counting()));
        for (Map.Entry<String, Long> entry : byCategory.entrySet()) {
            csv.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
        }
        csv.append("\n");

        // 3. Donation Status Breakdown
        csv.append("DONATION STATUS BREAKDOWN\n");
        csv.append("STATUS,COUNT\n");
        Map<String, Long> byStatus = allDonations.stream()
                .collect(Collectors.groupingBy(d -> d.getStatus() != null ? d.getStatus().name() : "UNKNOWN", Collectors.counting()));
        for (Map.Entry<String, Long> entry : byStatus.entrySet()) {
            csv.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
        }

        return csv.toString();
    }
}
