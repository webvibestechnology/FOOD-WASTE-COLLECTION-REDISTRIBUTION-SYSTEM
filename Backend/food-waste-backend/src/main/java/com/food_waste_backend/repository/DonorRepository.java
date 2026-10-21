package com.food_waste_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.food_waste_backend.Entity.Donor;
import com.food_waste_backend.Entity.User;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByUser(User user);

    @Query("SELECT d FROM Donor d WHERE d.user.id = :userId")
    Optional<Donor> findByUserId(Long userId);
}