package com.food_waste_backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.food_waste_backend.Entity.FoodItem;
import com.food_waste_backend.dto.FoodResponse;
import com.food_waste_backend.repository.FoodItemRepository;
import com.food_waste_backend.service.FoodService;

@Service
public class FoodServiceImpl implements FoodService {

    private final FoodItemRepository foodItemRepository;

    public FoodServiceImpl(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    @Override
    public List<FoodResponse> getFoodByDonationId(Long donationId) {
        List<FoodItem> items = foodItemRepository.findByDonationId(donationId);
        return items.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private FoodResponse mapToResponse(FoodItem item) {
        FoodResponse response = new FoodResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setQuantity(item.getQuantity() != null ? item.getQuantity().intValue() : null);
        response.setUnit(item.getUnit());
        response.setCategory(item.getCategory() != null ? item.getCategory().name() : null);
        return response;
    }
}
