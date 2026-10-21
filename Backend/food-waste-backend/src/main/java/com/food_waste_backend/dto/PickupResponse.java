package com.food_waste_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class PickupResponse {

    private Long id;
    private Long donationId;
    private Long foodRequestId;
    private String donationTitle;
    private String description;
    private String category;
    private BigDecimal quantity;
    private String quantityUnit;
    private String pickupAddress;
    private String deliveryAddress;
    private String donorName;
    private String donorPhone;
    private String ngoName;
    private String ngoPhone;
    private Long volunteerId;
    private String volunteerName;
    private String volunteerPhone;
    private String status;
    private LocalDateTime scheduledTime;
    private LocalDateTime collectedTime;
    private LocalDateTime completedTime;
    private String notes;
    private String handoverNotes;

    // Nested structures for frontend template compatibility (pickup.donation.*, pickup.donor.*, pickup.ngo.*)
    private Map<String, Object> donation;
    private Map<String, Object> donor;
    private Map<String, Object> ngo;

    public PickupResponse() {
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

    public Long getFoodRequestId() {
        return foodRequestId;
    }

    public void setFoodRequestId(Long foodRequestId) {
        this.foodRequestId = foodRequestId;
    }

    public String getDonationTitle() {
        return donationTitle;
    }

    public void setDonationTitle(String donationTitle) {
        this.donationTitle = donationTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorPhone() {
        return donorPhone;
    }

    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }

    public String getNgoPhone() {
        return ngoPhone;
    }

    public void setNgoPhone(String ngoPhone) {
        this.ngoPhone = ngoPhone;
    }

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public String getVolunteerPhone() {
        return volunteerPhone;
    }

    public void setVolunteerPhone(String volunteerPhone) {
        this.volunteerPhone = volunteerPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getCollectedTime() {
        return collectedTime;
    }

    public void setCollectedTime(LocalDateTime collectedTime) {
        this.collectedTime = collectedTime;
    }

    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getHandoverNotes() {
        return handoverNotes;
    }

    public void setHandoverNotes(String handoverNotes) {
        this.handoverNotes = handoverNotes;
    }

    public Map<String, Object> getDonation() {
        return donation;
    }

    public void setDonation(Map<String, Object> donation) {
        this.donation = donation;
    }

    public Map<String, Object> getDonor() {
        return donor;
    }

    public void setDonor(Map<String, Object> donor) {
        this.donor = donor;
    }

    public Map<String, Object> getNgo() {
        return ngo;
    }

    public void setNgo(Map<String, Object> ngo) {
        this.ngo = ngo;
    }
}
