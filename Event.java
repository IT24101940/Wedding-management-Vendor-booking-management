package com.dreamydayplanner.model;

import java.time.LocalDateTime;

public class Event {
    private Long id;
    private String eventType;
    private LocalDateTime eventDate;
    private String location;
    private String notes;
    private LocalDateTime createdDate;

    // Constructors
    public Event() {
        this.createdDate = LocalDateTime.now();
    }

    public Event(String eventType, LocalDateTime eventDate, String location, String notes) {
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.location = location;
        this.notes = notes;
        this.createdDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}