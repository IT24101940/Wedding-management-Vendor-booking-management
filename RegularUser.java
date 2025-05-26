package com.dreamydayplanner.model;

import java.time.LocalDateTime;

public class RegularUser extends User {
    private String weddingDate;
    private String partnerName;
    private Double budget;
    private Integer guestCount;
    private String venue;

    // Constructors
    public RegularUser() {
        super();
    }

    public RegularUser(String firstName, String lastName, String email, String password, String weddingDate, String partnerName, Double budget, Integer guestCount, String venue) {
        super(firstName, lastName, email, password);
        this.weddingDate = weddingDate;
        this.partnerName = partnerName;
        this.budget = budget;
        this.guestCount = guestCount;
        this.venue = venue;
    }

    // Getters and Setters
    public String getWeddingDate() { return weddingDate; }
    public void setWeddingDate(String weddingDate) { this.weddingDate = weddingDate; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public void setLastLogin(LocalDateTime now) {
    }
}