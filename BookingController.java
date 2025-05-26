package com.dreamydayplanner.controller;

import com.dreamydayplanner.model.Booking;
import com.dreamydayplanner.model.Event;
import com.dreamydayplanner.model.Vendor;
import com.dreamydayplanner.service.BookingService;
import com.dreamydayplanner.service.EventService;
import com.dreamydayplanner.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private EventService eventService;

    @GetMapping("/add")
    public String showAddBookingForm(Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("vendors", vendorService.getAllVendors());
        model.addAttribute("events", eventService.getAllEvents());
        return "booking/add-booking";
    }

    @PostMapping("/add")
    public String addBooking(@ModelAttribute("booking") Booking booking, @RequestParam Long vendorId, @RequestParam Long eventId, Model model) {
        Vendor vendor = vendorService.getAllVendors().stream().filter(v -> v.getId().equals(vendorId)).findFirst().orElse(null);
        Event event = eventService.getAllEvents().stream().filter(e -> e.getId().equals(eventId)).findFirst().orElse(null);

        if (vendor == null || event == null) {
            model.addAttribute("message", "Invalid vendor or event selected.");
            model.addAttribute("vendors", vendorService.getAllVendors());
            model.addAttribute("events", eventService.getAllEvents());
            return "booking/add-booking";
        }

        booking.setVendor(vendor);
        booking.setEvent(event);
        try {
            booking.setBookingDate(LocalDateTime.parse(booking.getBookingDate().toString()));
            booking.setStatus("Pending");
            bookingService.addBooking(booking);
            model.addAttribute("message", "Booking added successfully!");
            return "redirect:/booking/list";
        } catch (Exception e) {
            model.addAttribute("message", "Invalid date format. Use YYYY-MM-DD  HH:MM (e.g., 2025-12-15T14:30).");
            model.addAttribute("vendors", vendorService.getAllVendors());
            model.addAttribute("events", eventService.getAllEvents());
            return "booking/add-booking";
        }
    }

    @GetMapping("/update/{id}")
    public String showUpdateBookingForm(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getAllBookings().stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
        if (booking == null) {
            model.addAttribute("message", "Booking not found.");
            return "redirect:/booking/list";
        }
        model.addAttribute("booking", booking);
        model.addAttribute("vendors", vendorService.getAllVendors());
        model.addAttribute("events", eventService.getAllEvents());
        return "booking/update-booking";
    }

    @PostMapping("/update/{id}")
    public String updateBooking(@PathVariable Long id, @ModelAttribute("booking") Booking updatedBooking, @RequestParam Long vendorId, @RequestParam Long eventId, Model model) {
        Booking booking = bookingService.getAllBookings().stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
        if (booking == null) {
            model.addAttribute("message", "Booking not found.");
            return "redirect:/booking/list";
        }

        Vendor vendor = vendorService.getAllVendors().stream().filter(v -> v.getId().equals(vendorId)).findFirst().orElse(null);
        Event event = eventService.getAllEvents().stream().filter(e -> e.getId().equals(eventId)).findFirst().orElse(null);

        if (vendor == null || event == null) {
            model.addAttribute("message", "Invalid vendor or event selected.");
            model.addAttribute("booking", booking);
            model.addAttribute("vendors", vendorService.getAllVendors());
            model.addAttribute("events", eventService.getAllEvents());
            return "booking/update-booking";
        }

        try {
            booking.setVendor(vendor);
            booking.setEvent(event);
            booking.setBookingDate(LocalDateTime.parse(updatedBooking.getBookingDate().toString()));
            booking.setStatus(updatedBooking.getStatus());
            bookingService.addBooking(booking); // This will update the booking
            model.addAttribute("message", "Booking updated successfully!");
            return "redirect:/booking/list";
        } catch (Exception e) {
            model.addAttribute("message", "Invalid date format. Use YYYY-MM-DDTHH:MM (e.g., 2025-12-15T14:30).");
            model.addAttribute("booking", booking);
            model.addAttribute("vendors", vendorService.getAllVendors());
            model.addAttribute("events", eventService.getAllEvents());
            return "booking/update-booking";
        }
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getAllBookings().stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
        if (booking == null) {
            model.addAttribute("message", "Booking not found.");
            return "redirect:/booking/list";
        }
        bookingService.deleteBooking(id);
        model.addAttribute("message", "Booking canceled successfully!");
        return "redirect:/booking/list";
    }

    @GetMapping("/list")
    public String getAllBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "booking/booking-list";
    }
}