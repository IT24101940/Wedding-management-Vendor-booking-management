package com.dreamydayplanner.repository;

import com.dreamydayplanner.model.AdminUser;
import com.dreamydayplanner.model.RegularUser;
import com.dreamydayplanner.model.User;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;

@Repository
public class UserRepository {
    private LinkedList<User> users = new LinkedList<>();
    private Long idCounter = 1L;
    private static final String USERS_FILE = "src/main/resources/users.txt";
    private static final String LOGIN_FILE = "src/main/resources/login.txt";


    // Load users from users.txt on startup
    public UserRepository() {
        loadUsersFromFile();
    }

    private void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 7) continue; // Minimum fields for User

                Long id = Long.parseLong(parts[0]);
                String firstName = parts[1];
                String lastName = parts[2];
                String email = parts[3];
                String password = parts[4];
                String role = parts[5].trim();
                LocalDateTime registrationDate = LocalDateTime.parse(parts[6]);

                User user;
                if ("ADMIN".equals(role) && parts.length >= 11) { // Check for admin-specific fields
                    String adminRole = parts[7];
                    LocalDateTime lastLogin = LocalDateTime.parse(parts[8]);
                    String department = parts[9];
                    boolean active = Boolean.parseBoolean(parts[10]);
                    user = new AdminUser(firstName, lastName, email, password, adminRole, lastLogin, department, active);
                } else if ("USER".equals(role) && parts.length >= 12) { // Check for regular user-specific fields
                    String weddingDate = parts[7];
                    String partnerName = parts[8];
                    Double budget = parts[9].isEmpty() ? null : Double.parseDouble(parts[9]);
                    Integer guestCount = parts[10].isEmpty() ? null : Integer.parseInt(parts[10]);
                    String venue = parts[11];
                    user = new RegularUser(firstName, lastName, email, password, weddingDate, partnerName, budget, guestCount, venue);
                } else {
                    user = new User(firstName, lastName, email, password); // Fallback to base User
                }
                user.setId(id);
                user.setRegistrationDate(registrationDate);
                users.add(user);
                idCounter = Math.max(idCounter, id + 1);
            }
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
        }
    }

    private void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                if (user instanceof AdminUser) {
                    AdminUser admin = (AdminUser) user;
                    String line = String.format("%d,%s,%s,%s,%s,ADMIN,%s,%s,%s,%s,%b",
                            user.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getPassword(),
                            user.getRegistrationDate(),
                            admin.getAdminRole(),
                            admin.getLastLogin(),
                            admin.getDepartment(),
                            admin.isActive());
                    writer.write(line);
                } else if (user instanceof RegularUser) {
                    RegularUser regular = (RegularUser) user;
                    String line = String.format("%d,%s,%s,%s,%s,USER,%s,%s,%s,%s,%s,%s",
                            user.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getPassword(),
                            user.getRegistrationDate(),
                            regular.getWeddingDate() != null ? regular.getWeddingDate() : "",
                            regular.getPartnerName() != null ? regular.getPartnerName() : "",
                            regular.getBudget() != null ? regular.getBudget().toString() : "",
                            regular.getGuestCount() != null ? regular.getGuestCount().toString() : "",
                            regular.getVenue() != null ? regular.getVenue() : "");
                    writer.write(line);
                } else {
                    String line = String.format("%d,%s,%s,%s,%s,USER,%s",
                            user.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getPassword(),
                            user.getRegistrationDate());
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users to file: " + e.getMessage());
        }
    }

    // Save a user (create or update)
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idCounter++);
            if (user instanceof AdminUser) {
                AdminUser admin = (AdminUser) user;
                if (admin.getLastLogin() == null) {
                    admin.setLastLogin(LocalDateTime.now()); // Set to 2025-05-23T13:42:00+05:30
                }
            }
            users.add(user);
        } else {
            int index = -1;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(user.getId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                users.set(index, user);
            }
        }
        sortUsersByRegistrationDate();
        saveUsersToFile();
        return user;
    }

    // Find all users
    public LinkedList<User> findAll() {
        return new LinkedList<>(users);
    }

    // Find a user by ID
    public User findById(Long id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    // Find a user by email
    public User findByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).findFirst().orElse(null);
    }

    // Delete a user by ID
    public void deleteById(Long id) {
        users.removeIf(u -> u.getId().equals(id));
        saveUsersToFile();
    }

    private void sortUsersByRegistrationDate() {
        int n = users.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (users.get(j).getRegistrationDate().isAfter(users.get(j + 1).getRegistrationDate())) {
                    Collections.swap(users, j, j + 1);
                }
            }
        }
    }
}