package com.dreamydayplanner.service;

import com.dreamydayplanner.model.Booking;
import com.dreamydayplanner.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class BookingService {
    private final BookingRepository bookingRepository = new BookingRepository();

    public Booking addBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public LinkedList<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}