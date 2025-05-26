package com.dreamydayplanner.service;

import com.dreamydayplanner.model.Feedback;
import com.dreamydayplanner.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback addFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public LinkedList<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public Feedback findById(Long id) {
        return feedbackRepository.findById(id);
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}