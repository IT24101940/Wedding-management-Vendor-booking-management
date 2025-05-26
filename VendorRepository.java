package com.dreamydayplanner.repository;

import com.dreamydayplanner.model.Vendor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;

@Repository
public class VendorRepository {
    private LinkedList<Vendor> vendors = new LinkedList<>();
    private Long idCounter = 1L;
    private static final String VENDORS_FILE = "src/main/resources/vendors.txt";

    public VendorRepository() {
        loadVendorsFromFile();
    }

    private void loadVendorsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(VENDORS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1); // Include empty fields
                if (parts.length < 7) continue; // Ensure enough fields

                try {
                    Long id = Long.parseLong(parts[0]);
                    String name = parts[1];
                    String category = parts[2];
                    String contactEmail = parts[3];
                    Double price = Double.parseDouble(parts[4]);
                    String location = parts[5];
                    LocalDateTime addedDate = LocalDateTime.parse(parts[6]);

                    Vendor vendor = new Vendor(name, category, contactEmail, price, location);
                    vendor.setId(id);
                    vendor.setAddedDate(addedDate);
                    vendors.add(vendor);
                    idCounter = Math.max(idCounter, id + 1);
                } catch (Exception e) {
                    System.err.println("Error parsing vendor line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading vendors from file: " + e.getMessage());
        }
    }

    private void saveVendorsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VENDORS_FILE))) {
            for (Vendor vendor : vendors) {
                String line = String.format("%d,%s,%s,%s,%.2f,%s,%s",
                        vendor.getId(),
                        vendor.getName() != null ? vendor.getName() : "",
                        vendor.getCategory() != null ? vendor.getCategory() : "",
                        vendor.getContactEmail() != null ? vendor.getContactEmail() : "",
                        vendor.getPrice() != null ? vendor.getPrice() : 0.0,
                        vendor.getLocation() != null ? vendor.getLocation() : "",
                        vendor.getAddedDate());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving vendors to file: " + e.getMessage());
        }
    }

    public Vendor save(Vendor vendor) {
        if (vendor.getId() == null) {
            vendor.setId(idCounter++);
            vendors.add(vendor);
        } else {
            int index = -1;
            for (int i = 0; i < vendors.size(); i++) {
                if (vendors.get(i).getId().equals(vendor.getId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                vendors.set(index, vendor);
            }
        }
        sortVendorsByPrice();
        saveVendorsToFile();
        return vendor;
    }

    public LinkedList<Vendor> findAll() {
        return new LinkedList<>(vendors);
    }

    public Vendor findById(Long id) {
        return vendors.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    public void deleteById(Long id) {
        vendors.removeIf(v -> v.getId().equals(id));
        saveVendorsToFile();
    }

    private void sortVendorsByPrice() {
        int n = vendors.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (vendors.get(j).getPrice() > vendors.get(j + 1).getPrice()) {
                    Collections.swap(vendors, j, j + 1);
                }
            }
        }
    }
}