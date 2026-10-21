package com.food_waste_backend.dto;

import java.time.LocalDateTime;

public class PickupRequestDto {

    private Long donationId;
    private LocalDateTime scheduledTime;

    public PickupRequestDto() {
    }

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}