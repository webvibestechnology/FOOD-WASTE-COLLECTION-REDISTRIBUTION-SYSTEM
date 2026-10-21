package com.food_waste_backend.dto;

import java.time.LocalDateTime;

public class NgoResponse {

    private Long id;
    private String ngoName;
    private String organizationName;
    private String registrationNumber;
    private boolean verified;
    private String contactPerson;
    private String phone;
    private String description;
    private LocalDateTime registeredAt;

    public NgoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
        this.organizationName = ngoName;
    }

    public String getOrganizationName() {
        return organizationName != null ? organizationName : ngoName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
        if (this.ngoName == null) {
            this.ngoName = organizationName;
        }
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
