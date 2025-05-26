package com.dreamydayplanner.repository;

import com.dreamydayplanner.model.Event;
import java.io.*;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.LinkedList;

public class EventRepository {
    private LinkedList<Event> events = new LinkedList<>();
    private Long idCounter = 1L;
    private static final String EVENTS_FILE = "src/main/resources/events.txt";

    public EventRepository() {
        loadEventsFromFile();
    }

    private void loadEventsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(EVENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1); // Include empty fields
                if (parts.length < 6) continue;

                try {
                    Long id = Long.parseLong(parts[0]);
                    String eventType = parts[1];
                    LocalDateTime eventDate = LocalDateTime.parse(parts[2]);
                    String location = parts[3];
                    String notes = parts[4].isEmpty() ? null : parts[4];
                    LocalDateTime createdDate = LocalDateTime.parse(parts[5]);

                    Event event = new Event(eventType, eventDate, location, notes);
                    event.setId(id);
                    event.setCreatedDate(createdDate);
                    events.add(event);
                    idCounter = Math.max(idCounter, id + 1);
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading events from file: " + e.getMessage());
        }
    }

    private void saveEventsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EVENTS_FILE))) {
            for (Event event : events) {
                String line = String.format("%d,%s,%s,%s,%s,%s",
                        event.getId(),
                        event.getEventType(),
                        event.getEventDate(),
                        event.getLocation(),
                        event.getNotes() != null ? event.getNotes() : "",
                        event.getCreatedDate());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving events to file: " + e.getMessage());
        }
    }

    public Event save(Event event) {
        if (event.getId() == null) {
            event.setId(idCounter++);
            events.add(event);
        } else {
            int index = -1;
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getId().equals(event.getId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                events.set(index, event);
            }
        }
        sortEventsByDate();
        saveEventsToFile();
        return event;
    }

    public LinkedList<Event> findAll() {
        return new LinkedList<>(events);
    }

    public Event findById(Long id) {
        return events.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }

    public void deleteById(Long id) {
        events.removeIf(e -> e.getId().equals(id));
        saveEventsToFile();
    }

    private void sortEventsByDate() {
        int n = events.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (events.get(j).getEventDate().isAfter(events.get(j + 1).getEventDate())) {
                    Collections.swap(events, j, j + 1);
                }
            }
        }
    }
}