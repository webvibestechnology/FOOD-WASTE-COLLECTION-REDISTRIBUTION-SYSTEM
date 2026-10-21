package com.food_waste_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class FoodRequestDto {

    @NotNull(message = "Donation ID is required")
    private Long donationId;

    private BigDecimal requestedQuantity;

    private Integer beneficiaryCount;

    private String deliveryNotes;

    private String notes;

    public FoodRequestDto() {
    }

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    public BigDecimal getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(BigDecimal requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public Integer getBeneficiaryCount() {
        return beneficiaryCount;
    }

    public void setBeneficiaryCount(Integer beneficiaryCount) {
        this.beneficiaryCount = beneficiaryCount;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
