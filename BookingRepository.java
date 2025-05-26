package com.dreamydayplanner.repository;

import com.dreamydayplanner.model.Booking;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;

public class BookingRepository {
    private LinkedList<Booking> bookings = new LinkedList<>();
    private Long idCounter = 1L;
    private static final String BOOKINGS_FILE = "src/main/resources/bookings.txt";
    // Load bookings from bookings.txt on startup
    public BookingRepository() {
        loadBookingsFromFile();
    }

    private void loadBookingsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 7) continue; // Ensure enough fields for basic Booking

                Long id = Long.parseLong(parts[0]);
                Long vendorId = Long.parseLong(parts[1]);
                Long eventId = Long.parseLong(parts[2]);
                LocalDateTime bookingDate = LocalDateTime.parse(parts[3]);
                String status = parts[4];
                LocalDateTime createdDate = LocalDateTime.parse(parts[5]);

                // Note: Vendor and Event objects are not fully loaded here; this is a simplified approach.
                // In a real system, you'd need to fetch Vendor and Event by ID from their repositories.
                Booking booking = new Booking();
                booking.setId(id);
                booking.setBookingDate(bookingDate);
                booking.setStatus(status);
                booking.setCreatedDate(createdDate);
                bookings.add(booking);
                idCounter = Math.max(idCounter, id + 1);
            }
        } catch (IOException e) {
            System.err.println("Error loading bookings from file: " + e.getMessage());
        }
    }

    private void saveBookingsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking booking : bookings) {
                String line = String.format("%d,%d,%d,%s,%s,%s",
                        booking.getId(),
                        booking.getVendor() != null ? booking.getVendor().getId() : 0,
                        booking.getEvent() != null ? booking.getEvent().getId() : 0,
                        booking.getBookingDate(),
                        booking.getStatus(),
                        booking.getCreatedDate());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving bookings to file: " + e.getMessage());
        }
    }

    // Save a booking (create or update)
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(idCounter++);
            bookings.add(booking);
        } else {
            int index = -1;
            for (int i = 0; i < bookings.size(); i++) {
                if (bookings.get(i).getId().equals(booking.getId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                bookings.set(index, booking);
            }
        }
        sortBookingsByDate();
        saveBookingsToFile();
        return booking;
    }

    // Find all bookings
    public LinkedList<Booking> findAll() {
        return new LinkedList<>(bookings); // Return a copy to prevent external modification
    }

    // Find a booking by ID
    public Booking findById(Long id) {
        return bookings.stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
    }

    // Delete a booking by ID
    public void deleteById(Long id) {
        bookings.removeIf(b -> b.getId().equals(id));
        saveBookingsToFile();
    }

    private void sortBookingsByDate() {
        int n = bookings.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (bookings.get(j).getBookingDate().isAfter(bookings.get(j + 1).getBookingDate())) {
                    Collections.swap(bookings, j, j + 1);
                }
            }
        }
    }
}