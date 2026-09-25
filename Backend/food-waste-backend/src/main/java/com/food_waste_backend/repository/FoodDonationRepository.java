package com.food_waste_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.food_waste_backend.Entity.FoodDonation;
import com.food_waste_backend.enums.DonationStatus;

public interface FoodDonationRepository extends JpaRepository<FoodDonation, Long> {

   
    List<FoodDonation> findByDonorId(Long donorId);

 
    List<FoodDonation> findByStatus(DonationStatus status);
}