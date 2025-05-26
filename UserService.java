package com.dreamydayplanner.service;

import com.dreamydayplanner.model.AdminUser;
import com.dreamydayplanner.model.RegularUser;
import com.dreamydayplanner.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private List<User> users = new ArrayList<>();
    private Long idCounter = 1L;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService() {
        // Temporary admin user creation on startup
        if (users.isEmpty()) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRegistrationDate(LocalDateTime.now());
            AdminUser adminUser = new AdminUser();
            copyUserFields(admin, adminUser);
            adminUser.setAdminRole("Manager");
            adminUser.setDepartment("Operations");
            adminUser.setActive(true);
            users.add(adminUser);
            logActivity("Registered admin: " + adminUser.getEmail() + ", ID: " + adminUser.getId() + " at " + LocalDateTime.now());
        }
    }

    public User findByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).findFirst().orElse(null);
    }

    public void registerUser(
            User user,
            String role,
            String adminRole,
            String department,
            String weddingDate,
            String partnerName,
            Double budget,
            Integer guestCount,
            String venue) {
        user.setId(idCounter++);
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setRegistrationDate(LocalDateTime.now());

        if ("ADMIN".equals(role)) {
            AdminUser adminUser = new AdminUser();
            copyUserFields(user, adminUser);
            adminUser.setAdminRole(adminRole);
            adminUser.setDepartment(department);
            adminUser.setActive(true);
            users.add(adminUser);
            logActivity("Registered admin: " + adminUser.getEmail() + ", ID: " + adminUser.getId() + " at " + LocalDateTime.now());
        } else if ("MANAGER".equals(role)) {
            AdminUser managerUser = new AdminUser();
            copyUserFields(user, managerUser);
            managerUser.setAdminRole(adminRole);
            managerUser.setDepartment(department);
            managerUser.setActive(true);
            users.add(managerUser);
            logActivity("Registered manager: " + managerUser.getEmail() + ", ID: " + managerUser.getId() + " at " + LocalDateTime.now());
        } else {
            RegularUser regularUser = new RegularUser();
            copyUserFields(user, regularUser);
            regularUser.setWeddingDate(weddingDate);
            regularUser.setPartnerName(partnerName);
            regularUser.setBudget(budget);
            regularUser.setGuestCount(guestCount);
            regularUser.setVenue(venue);
            users.add(regularUser);
            logActivity("Registered user: " + regularUser.getEmail() + ", ID: " + regularUser.getId() + " at " + LocalDateTime.now());
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public void updateUser(
            User user,
            String adminRole,
            String department,
            Boolean active,
            String weddingDate,
            String partnerName,
            Double budget,
            Integer guestCount,
            String venue) {
        User existingUser = users.stream().filter(u -> u.getId().equals(user.getId())).findFirst().orElse(null);
        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            existingUser.setPassword(hashedPassword);
            if (existingUser instanceof AdminUser adminUser) {
                adminUser.setAdminRole(adminRole);
                adminUser.setDepartment(department);
                adminUser.setActive(active);
                logActivity("Updated admin: " + adminUser.getEmail() + ", ID: " + adminUser.getId() + " at " + LocalDateTime.now());
            } else if (existingUser instanceof RegularUser regularUser) {
                regularUser.setWeddingDate(weddingDate);
                regularUser.setPartnerName(partnerName);
                regularUser.setBudget(budget);
                regularUser.setGuestCount(guestCount);
                regularUser.setVenue(venue);
                logActivity("Updated user: " + regularUser.getEmail() + ", ID: " + regularUser.getId() + " at " + LocalDateTime.now());
            }
        }
    }

    public void deleteUser(Long id) {
        User user = users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
        if (user != null) {
            users.remove(user);
            logActivity("Deleted user: " + user.getEmail() + ", ID: " + user.getId() + " at " + LocalDateTime.now());
        }
    }

    public void updateLastLogin(String email) {
        User user = findByEmail(email);
        if (user == null) {
            logActivity("Failed to update last login for email: " + email + " - User not found at " + LocalDateTime.now());
            return;
        }
        if (user instanceof AdminUser adminUser) {
            adminUser.setLastLogin(LocalDateTime.now());
            logActivity("Admin last login updated: " + email + ", ID: " + adminUser.getId() + " at " + LocalDateTime.now());
        } else if (user instanceof RegularUser regularUser) {
            regularUser.setLastLogin(LocalDateTime.now());
            logActivity("User last login updated: " + email + ", ID: " + regularUser.getId() + " at " + LocalDateTime.now());
        }
    }

    private void copyUserFields(User source, User target) {
        target.setId(source.getId());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setEmail(source.getEmail());
        target.setPassword(source.getPassword());
        target.setRegistrationDate(source.getRegistrationDate());
    }

    private void logActivity(String activity) {
        System.out.println(activity);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(activity);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write to users.txt: " + e.getMessage());
            e.printStackTrace();
        }
    }
}