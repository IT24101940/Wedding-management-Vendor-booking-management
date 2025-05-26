package com.dreamydayplanner.model;

import java.time.LocalDateTime;

public class AdminUser extends User {
    private String adminRole;
    private LocalDateTime lastLogin;
    private String department;
    private boolean active;

    // Constructors
    public AdminUser() {
        super();
        this.lastLogin = LocalDateTime.now(); // Set to 2025-05-22T15:47:00+05:30
        this.active = true;
    }

    public AdminUser(String firstName, String lastName, String email, String password, String adminRole, LocalDateTime lastLogin, String department, boolean active) {
        super(firstName, lastName, email, password);
        this.adminRole = adminRole;
        this.lastLogin = lastLogin != null ? lastLogin : LocalDateTime.now();
        this.department = department;
        this.active = active;
    }

    // Getters and Setters
    public String getAdminRole() { return adminRole; }
    public void setAdminRole(String adminRole) { this.adminRole = adminRole; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}