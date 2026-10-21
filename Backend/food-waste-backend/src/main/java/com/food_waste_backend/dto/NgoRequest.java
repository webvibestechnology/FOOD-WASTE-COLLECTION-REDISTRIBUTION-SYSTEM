package com.food_waste_backend.dto;

public class NgoRequest {

    private String ngoName;
    private String registrationNumber;
    private String description;
    private String contactPerson;

    public NgoRequest() {
    }

    public NgoRequest(String ngoName, String registrationNumber, String description, String contactPerson) {
        this.ngoName = ngoName;
        this.registrationNumber = registrationNumber;
        this.description = description;
        this.contactPerson = contactPerson;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
}
