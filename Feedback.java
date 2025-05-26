package com.dreamydayplanner.model;

import java.time.LocalDateTime;

public class Feedback {
    private Long id;
    private Vendor vendor;
    private String userEmail; // Links to the user who submitted the feedback
    private int rating; // 1 to 5
    private String comments;
    private LocalDateTime submissionDate;

    // Constructors
    public Feedback() {
        this.submissionDate = LocalDateTime.now(); // Set to 2025-05-22T12:48:00+05:30
    }

    public Feedback(Vendor vendor, String userEmail, int rating, String comments) {
        this.vendor = vendor;
        this.userEmail = userEmail;
        this.rating = rating;
        this.comments = comments;
        this.submissionDate = LocalDateTime.now();
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
}