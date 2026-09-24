package com.food_waste_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.food_waste_backend.Entity.Ngo;

public interface NgoRepository extends JpaRepository<Ngo, Long> {

    Optional<Ngo> findByUserId(Long userId);

    List<Ngo> findByVerifiedTrue();
}