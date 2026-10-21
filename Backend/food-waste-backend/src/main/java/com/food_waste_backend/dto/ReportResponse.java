package com.food_waste_backend.dto;

public class ReportResponse {

    private Long totalDonations;
    private Long totalPickups;
    private Long totalRequests;
    private Long activeVolunteers;
    private Long activeNgos;
    private Long totalUsers;
    private Long totalDonors;
    private Long totalNgos;

    public ReportResponse() {
    }

    public Long getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(Long totalDonations) {
        this.totalDonations = totalDonations;
    }

    public Long getTotalPickups() {
        return totalPickups;
    }

    public void setTotalPickups(Long totalPickups) {
        this.totalPickups = totalPickups;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Long getActiveVolunteers() {
        return activeVolunteers;
    }

    public void setActiveVolunteers(Long activeVolunteers) {
        this.activeVolunteers = activeVolunteers;
    }

    public Long getActiveNgos() {
        return activeNgos;
    }

    public void setActiveNgos(Long activeNgos) {
        this.activeNgos = activeNgos;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalDonors() {
        return totalDonors;
    }

    public void setTotalDonors(Long totalDonors) {
        this.totalDonors = totalDonors;
    }

    public Long getTotalNgos() {
        return totalNgos;
    }

    public void setTotalNgos(Long totalNgos) {
        this.totalNgos = totalNgos;
    }
}