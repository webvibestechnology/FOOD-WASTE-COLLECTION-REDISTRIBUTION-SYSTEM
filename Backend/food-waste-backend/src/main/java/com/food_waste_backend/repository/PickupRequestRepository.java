package com.food_waste_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.food_waste_backend.Entity.PickupRequest;

public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {

    List<PickupRequest> findByVolunteerId(Long volunteerId);

    List<PickupRequest> findByDonationId(Long donationId);
}