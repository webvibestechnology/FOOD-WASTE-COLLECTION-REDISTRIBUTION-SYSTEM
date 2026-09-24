package com.food_waste_backend.Entity;

import java.time.LocalDateTime;

import com.food_waste_backend.enums.RequestStatus;

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
@Table(name = "food_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ngo_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Ngo ngo;

    @ManyToOne
    @JoinColumn(name = "donation_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FoodDonation donation;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private String notes;

    private LocalDateTime requestedAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (status == null) {
            status = RequestStatus.PENDING;
        }

        requestedAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}