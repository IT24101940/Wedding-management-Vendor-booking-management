package com.dreamydayplanner.controller;

import com.dreamydayplanner.model.Event;
import com.dreamydayplanner.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping("/add")
    public String showAddEventForm(Model model, Authentication auth) {
        if (auth == null || (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) &&
                !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")))) {
            return "redirect:/user/login";
        }
        model.addAttribute("event", new Event());
        return "event/add-event";
    }

    @PostMapping("/add")
    public String addEvent(@ModelAttribute("event") Event event, Model model, Authentication auth) {
        if (auth == null || (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) &&
                !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")))) {
            return "redirect:/user/login";
        }
        try {
            eventService.addEvent(event);
            model.addAttribute("message", "Event added successfully!");
            return "redirect:/event/list";
        } catch (Exception e) {
            model.addAttribute("message", "Error adding event: " + e.getMessage());
            return "event/add-event";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditEventForm(@PathVariable Long id, Model model, Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        Event event = eventService.findById(id);
        if (event == null) {
            model.addAttribute("message", "Event not found.");
            return "redirect:/event/list";
        }
        model.addAttribute("event", event);
        return "event/edit-event";
    }

    @PostMapping("/edit/{id}")
    public String editEvent(@PathVariable Long id, @ModelAttribute("event") Event event, Model model, Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        try {
            event.setId(id);
            eventService.updateEvent(event);
            model.addAttribute("message", "Event updated successfully!");
            return "redirect:/event/list";
        } catch (Exception e) {
            model.addAttribute("message", "Error updating event: " + e.getMessage());
            return "event/edit-event";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, Model model, Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        Event event = eventService.findById(id);
        if (event == null) {
            model.addAttribute("message", "Event not found.");
            return "redirect:/event/list";
        }
        eventService.deleteEvent(id);
        model.addAttribute("message", "Event deleted successfully!");
        return "redirect:/event/list";
    }

    @GetMapping("/list")
    public String getAllEvents(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("events", eventService.getAllEvents());
        return "event/event-list";
    }
}