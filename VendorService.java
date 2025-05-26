package com.dreamydayplanner.service;

import com.dreamydayplanner.model.Vendor;
import com.dreamydayplanner.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VendorService {
    @Autowired
    private VendorRepository vendorRepository;

    public Vendor addVendor(Vendor vendor) {
        vendor.setAddedDate(LocalDateTime.now());
        Vendor savedVendor = vendorRepository.save(vendor);
        logActivity("Added vendor: " + savedVendor.getName() + ", Category: " + savedVendor.getCategory() +
                ", Email: " + savedVendor.getContactEmail() + ", Price: " + savedVendor.getPrice() +
                ", Location: " + savedVendor.getLocation() + " at " + LocalDateTime.now());
        return savedVendor;
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public List<Vendor> searchVendors(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllVendors();
        }
        String lowerQuery = query.toLowerCase();
        return vendorRepository.findAll().stream()
                .filter(v -> (v.getName() != null && v.getName().toLowerCase().contains(lowerQuery)) ||
                        (v.getCategory() != null && v.getCategory().toLowerCase().contains(lowerQuery)) ||
                        (v.getLocation() != null && v.getLocation().toLowerCase().contains(lowerQuery)))
                .toList();
    }

    public Vendor findById(Long id) {
        return vendorRepository.findById(id);
    }

    public void updateVendor(Vendor updatedVendor) {
        Vendor existingVendor = findById(updatedVendor.getId());
        if (existingVendor != null) {
            existingVendor.setName(updatedVendor.getName());
            existingVendor.setCategory(updatedVendor.getCategory());
            existingVendor.setContactEmail(updatedVendor.getContactEmail());
            existingVendor.setPrice(updatedVendor.getPrice());
            existingVendor.setLocation(updatedVendor.getLocation());
            vendorRepository.save(existingVendor);
            logActivity("Updated vendor: " + existingVendor.getName() + ", Location: " +
                    existingVendor.getLocation() + " at " + LocalDateTime.now());
        }
    }

    public void deleteVendor(Long id) {
        Vendor vendor = findById(id);
        if (vendor != null) {
            vendorRepository.deleteById(id);
            logActivity("Deleted vendor: " + vendor.getName() + " at " + LocalDateTime.now());
        }
    }

    private void logActivity(String activity) {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter("vendor-activity.log", true))) {
            writer.write(activity);
            writer.newLine();
        } catch (java.io.IOException e) {
            System.err.println("Error logging activity: " + e.getMessage());
        }
    }
}