package com.food_waste_backend.Entity;

import java.time.LocalDateTime;

import com.food_waste_backend.enums.PickupStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "pickup_requests")
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "donation_id")
    private FoodDonation donation;

    @ManyToOne
    @JoinColumn(name = "food_request_id")
    private FoodRequest foodRequest;

    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = true)
    private Volunteer volunteer;

    @Enumerated(EnumType.STRING)
    private PickupStatus status;

    private LocalDateTime scheduledTime;

    private LocalDateTime collectedTime;

    private LocalDateTime completedTime;

    private String pickupAddress;

    private String deliveryAddress;

    private String contactPhone;

    @Column(length = 1000)
    private String notes;

    @Column(length = 1000)
    private String handoverNotes;

    private LocalDateTime createdAt;

    public PickupRequest() {
    }

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = PickupStatus.SCHEDULED;
        }
        createdAt = LocalDateTime.now();
        if (status == PickupStatus.COLLECTED && collectedTime == null) {
            collectedTime = LocalDateTime.now();
        }
        if (status == PickupStatus.COMPLETED && completedTime == null) {
            completedTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (status == PickupStatus.COLLECTED && collectedTime == null) {
            collectedTime = LocalDateTime.now();
        }
        if (status == PickupStatus.COMPLETED && completedTime == null) {
            completedTime = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FoodDonation getDonation() {
        return donation;
    }

    public void setDonation(FoodDonation donation) {
        this.donation = donation;
    }

    public FoodRequest getFoodRequest() {
        return foodRequest;
    }

    public void setFoodRequest(FoodRequest foodRequest) {
        this.foodRequest = foodRequest;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public PickupStatus getStatus() {
        return status;
    }

    public void setStatus(PickupStatus status) {
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

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
