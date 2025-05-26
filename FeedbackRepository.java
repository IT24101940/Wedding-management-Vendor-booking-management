package com.dreamydayplanner.repository;

import com.dreamydayplanner.model.Feedback;
import com.dreamydayplanner.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;

@Repository
public class FeedbackRepository {
    private LinkedList<Feedback> feedbacks = new LinkedList<>();
    private Long idCounter = 1L;
    private static final String FEEDBACKS_FILE = "src/main/resources/feedbacks.txt";

    @Autowired
    private VendorRepository vendorRepository;

    public FeedbackRepository() {
        loadFeedbacksFromFile();
    }

    private void loadFeedbacksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FEEDBACKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                try {
                    Long id = Long.parseLong(parts[0]);
                    Long vendorId = Long.parseLong(parts[1]);
                    String userEmail = parts[2];
                    int rating = Integer.parseInt(parts[3]);
                    String comments = parts[4].replace("\"", "");
                    LocalDateTime submissionDate = LocalDateTime.parse(parts[5]);

                    Feedback feedback = new Feedback();
                    feedback.setId(id);
                    Vendor vendor = vendorRepository.findById(vendorId);
                    feedback.setVendor(vendor);
                    feedback.setUserEmail(userEmail);
                    feedback.setRating(rating);
                    feedback.setComments(comments.isEmpty() ? null : comments);
                    feedback.setSubmissionDate(submissionDate);
                    feedbacks.add(feedback);
                    idCounter = Math.max(idCounter, id + 1);
                } catch (Exception e) {
                    System.err.println("Error parsing feedback line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading feedbacks from file: " + e.getMessage());
        }
    }

    private void saveFeedbacksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FEEDBACKS_FILE))) {
            for (Feedback feedback : feedbacks) {
                String comments = feedback.getComments() != null ? "\"" + feedback.getComments().replace("\"", "\"\"") + "\"" : "";
                String line = String.format("%d,%d,%s,%d,%s,%s",
                        feedback.getId(),
                        feedback.getVendor() != null ? feedback.getVendor().getId() : 0,
                        feedback.getUserEmail() != null ? feedback.getUserEmail() : "",
                        feedback.getRating(),
                        comments,
                        feedback.getSubmissionDate());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving feedbacks to file: " + e.getMessage());
        }
    }

    public Feedback save(Feedback feedback) {
        if (feedback.getId() == null) {
            feedback.setId(idCounter++);
            feedbacks.add(feedback);
        } else {
            int index = -1;
            for (int i = 0; i < feedbacks.size(); i++) {
                if (feedbacks.get(i).getId().equals(feedback.getId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                feedbacks.set(index, feedback);
            }
        }
        sortFeedbacksByDate();
        saveFeedbacksToFile();
        return feedback;
    }

    public LinkedList<Feedback> findAll() {
        return new LinkedList<>(feedbacks);
    }

    public Feedback findById(Long id) {
        return feedbacks.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
    }

    public void deleteById(Long id) {
        feedbacks.removeIf(f -> f.getId().equals(id));
        saveFeedbacksToFile();
    }

    private void sortFeedbacksByDate() {
        int n = feedbacks.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (feedbacks.get(j).getSubmissionDate().isBefore(feedbacks.get(j + 1).getSubmissionDate())) {
                    Collections.swap(feedbacks, j, j + 1);
                }
            }
        }
    }
}