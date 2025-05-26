package com.dreamydayplanner.model;

import java.time.LocalDateTime;

public class Booking {
    private Long id;
    private Vendor vendor;
    private Event event;
    private LocalDateTime bookingDate;
    private String status; // e.g., "Confirmed", "Pending"
    private LocalDateTime createdDate;

    // Constructors
    public Booking() {
        this.createdDate = LocalDateTime.now(); // Set to 2025-05-22T10:59:00+05:30
    }

    public Booking(Vendor vendor, Event event, LocalDateTime bookingDate, String status) {
        this.vendor = vendor;
        this.event = event;
        this.bookingDate = bookingDate;
        this.status = status;
        this.createdDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}