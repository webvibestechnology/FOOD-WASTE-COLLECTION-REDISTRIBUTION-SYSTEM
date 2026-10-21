package com.food_waste_backend.dto;

public class VolunteerRequest {

    private String vehicleType;
    private String operatingArea;

    public VolunteerRequest() {
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getOperatingArea() {
        return operatingArea;
    }

    public void setOperatingArea(String operatingArea) {
        this.operatingArea = operatingArea;
    }
}