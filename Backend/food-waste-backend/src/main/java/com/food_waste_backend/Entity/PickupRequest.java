package com.food_waste_backend.Entity;

import java.time.LocalDateTime;

import com.food_waste_backend.enums.PickupStatus;

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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pickup_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "donation_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FoodDonation donation;

    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Volunteer volunteer;

    @Enumerated(EnumType.STRING)
    private PickupStatus status;

    private LocalDateTime scheduledTime;

    private LocalDateTime completedTime;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        if (status == null) {
            status = PickupStatus.SCHEDULED;
        }

        createdAt = LocalDateTime.now();

        if (status == PickupStatus.COMPLETED && completedTime == null) {
            completedTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {

        if (status == PickupStatus.COMPLETED && completedTime == null) {
            completedTime = LocalDateTime.now();
        }
    }
}