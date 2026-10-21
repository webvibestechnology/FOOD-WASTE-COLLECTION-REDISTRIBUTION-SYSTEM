package com.food_waste_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.food_waste_backend.Entity.FoodRequest;
import com.food_waste_backend.enums.RequestStatus;

public interface FoodRequestRepository extends JpaRepository<FoodRequest, Long> {

    List<FoodRequest> findByNgoId(Long ngoId);

    List<FoodRequest> findByDonationId(Long donationId);

    List<FoodRequest> findByStatus(RequestStatus status);

    @Query("SELECT fr FROM FoodRequest fr WHERE fr.donation.donor.id = :donorId ORDER BY fr.requestedAt DESC")
    List<FoodRequest> findByDonorId(Long donorId);
}
