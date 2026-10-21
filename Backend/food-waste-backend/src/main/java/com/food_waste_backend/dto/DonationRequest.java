package com.food_waste_backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DonationRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "Quantity unit is required")
    private String quantityUnit;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Expiry time is required")
    @Future(message = "Expiry time must be in the future")
    private LocalDateTime expiryTime;

    private LocalDateTime preparationTime;

    private LocalDateTime safeUseDeadline;

    private String allergens;

    private String storageRequirements;

    private String dietaryInfo;

    private String pickupAddress;

    private String contactPhone;

    public DonationRequest() {
    }

    public DonationRequest(String title, String description, Integer quantity, String quantityUnit, String category, LocalDateTime expiryTime) {
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.category = category;
        this.expiryTime = expiryTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public LocalDateTime getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(LocalDateTime preparationTime) {
        this.preparationTime = preparationTime;
    }

    public LocalDateTime getSafeUseDeadline() {
        return safeUseDeadline;
    }

    public void setSafeUseDeadline(LocalDateTime safeUseDeadline) {
        this.safeUseDeadline = safeUseDeadline;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public String getStorageRequirements() {
        return storageRequirements;
    }

    public void setStorageRequirements(String storageRequirements) {
        this.storageRequirements = storageRequirements;
    }

    public String getDietaryInfo() {
        return dietaryInfo;
    }

    public void setDietaryInfo(String dietaryInfo) {
        this.dietaryInfo = dietaryInfo;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
