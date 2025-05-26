package com.dreamydayplanner.controller;

import com.dreamydayplanner.model.Feedback;
import com.dreamydayplanner.model.Vendor;
import com.dreamydayplanner.service.FeedbackService;
import com.dreamydayplanner.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private VendorService vendorService;

    @GetMapping("/add")
    public String showAddFeedbackForm(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("feedback", new Feedback());
        model.addAttribute("vendors", vendorService.getAllVendors());
        return "feedback/add-feedback";
    }

    @PostMapping("/add")
    public String addFeedback(@ModelAttribute("feedback") Feedback feedback, @RequestParam Long vendorId, Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/user/login";
        }
        Vendor vendor = vendorService.getAllVendors().stream().filter(v -> v.getId().equals(vendorId)).findFirst().orElse(null);
        if (vendor == null) {
            model.addAttribute("message", "Invalid vendor selected.");
            model.addAttribute("vendors", vendorService.getAllVendors());
            return "feedback/add-feedback";
        }

        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            model.addAttribute("message", "Rating must be between 1 and 5.");
            model.addAttribute("vendors", vendorService.getAllVendors());
            return "feedback/add-feedback";
        }

        feedback.setVendor(vendor);
        feedback.setUserEmail(auth != null ? auth.getName() : "anonymous");
        feedbackService.addFeedback(feedback);
        model.addAttribute("message", "Feedback submitted successfully!");
        return "redirect:/feedback/list";
    }

    @GetMapping("/list")
    public String getAllFeedbacks(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
        return "feedback/feedback-list";
    }

    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, Model model, Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        Feedback feedback = feedbackService.findById(id);
        if (feedback == null) {
            model.addAttribute("message", "Feedback not found.");
            return "redirect:/feedback/list";
        }
        feedbackService.deleteFeedback(id);
        model.addAttribute("message", "Feedback deleted successfully!");
        return "redirect:/feedback/list";
    }
}