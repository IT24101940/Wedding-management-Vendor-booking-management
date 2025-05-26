package com.dreamydayplanner.model;

import java.time.LocalDateTime;

public class Vendor {
    private Long id;
    private String name;
    private String category;
    private String contactEmail;
    private Double price;
    private String location;
    private LocalDateTime addedDate;

    public Vendor() {}

    public Vendor(String name, String category, String contactEmail, Double price, String location) {
        this.name = name;
        this.category = category;
        this.contactEmail = contactEmail;
        this.price = price;
        this.location = location;
        this.addedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDateTime addedDate) { this.addedDate = addedDate; }
}