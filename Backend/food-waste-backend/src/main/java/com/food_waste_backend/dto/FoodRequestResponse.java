package com.food_waste_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FoodRequestResponse {

    private Long id;
    private Long donationId;
    private String donationTitle;
    private Long ngoId;
    private String ngoName;
    private String status;
    private BigDecimal requestedQuantity;
    private Integer beneficiaryCount;
    private String deliveryNotes;
    private String notes;
    private LocalDateTime requestedAt;

    public FoodRequestResponse() {
    }

    public FoodRequestResponse(Long id, Long donationId, String donationTitle, Long ngoId, String ngoName, String status, String notes, LocalDateTime requestedAt) {
        this.id = id;
        this.donationId = donationId;
        this.donationTitle = donationTitle;
        this.ngoId = ngoId;
        this.ngoName = ngoName;
        this.status = status;
        this.notes = notes;
        this.requestedAt = requestedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    public String getDonationTitle() {
        return donationTitle;
    }

    public void setDonationTitle(String donationTitle) {
        this.donationTitle = donationTitle;
    }

    public Long getNgoId() {
        return ngoId;
    }

    public void setNgoId(Long ngoId) {
        this.ngoId = ngoId;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}
