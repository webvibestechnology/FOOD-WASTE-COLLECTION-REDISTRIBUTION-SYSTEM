package com.food_waste_backend.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.food_waste_backend.enums.DonationStatus;
import com.food_waste_backend.enums.FoodCategory;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "food_donations")
public class FoodDonation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String quantityUnit;

    private BigDecimal allocatedQuantity = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus status;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    private LocalDateTime preparationTime;

    private LocalDateTime safeUseDeadline;

    private String allergens;

    private String storageRequirements;

    private String dietaryInfo;

    private String pickupAddress;

    private String contactPhone;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(
        mappedBy = "donation",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<FoodItem> items = new ArrayList<>();

    public FoodDonation() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (status == null) {
            status = DonationStatus.AVAILABLE;
        }
        if (allocatedQuantity == null) {
            allocatedQuantity = BigDecimal.ZERO;
        }
        if (safeUseDeadline == null) {
            safeUseDeadline = expiryTime;
        }
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (allocatedQuantity == null) {
            allocatedQuantity = BigDecimal.ZERO;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
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

    public BigDecimal getAllocatedQuantity() {
        return allocatedQuantity != null ? allocatedQuantity : BigDecimal.ZERO;
    }

    public void setAllocatedQuantity(BigDecimal allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public BigDecimal getRemainingQuantity() {
        BigDecimal total = quantity != null ? quantity : BigDecimal.ZERO;
        BigDecimal allocated = allocatedQuantity != null ? allocatedQuantity : BigDecimal.ZERO;
        BigDecimal rem = total.subtract(allocated);
        return rem.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : rem;
    }

    public FoodCategory getCategory() {
        return category;
    }

    public void setCategory(FoodCategory category) {
        this.category = category;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
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
        return safeUseDeadline != null ? safeUseDeadline : expiryTime;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<FoodItem> getItems() {
        return items;
    }

    public void setItems(List<FoodItem> items) {
        this.items = items;
    }
}
