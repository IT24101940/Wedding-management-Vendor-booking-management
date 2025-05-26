package com.dreamydayplanner.controller;

import com.dreamydayplanner.model.User;
import com.dreamydayplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user,
            @ModelAttribute("weddingDate") String weddingDate,
            @ModelAttribute("partnerName") String partnerName,
            @ModelAttribute("budget") String budgetStr,
            @ModelAttribute("guestCount") String guestCountStr, // Changed to String
            @ModelAttribute("venue") String venue,
            Model model) {
        Double budget = null;
        Integer guestCount = null;
        try {
            if (budgetStr != null && !budgetStr.isEmpty()) {
                budget = Double.parseDouble(budgetStr);
            }
            if (guestCountStr != null && !guestCountStr.isEmpty()) {
                guestCount = Integer.parseInt(guestCountStr); // Convert String to Integer
            }
        } catch (NumberFormatException e) {
            model.addAttribute("message", "Invalid budget or guest count format. Please enter valid numbers.");
            return "user/register";
        }
        userService.registerUser(user, "USER", null, null, weddingDate, partnerName, budget, guestCount, venue);
        model.addAttribute("message", "Registration successful! Please log in.");
        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "User not found. Please register or check your email.");
            return "user/login";
        }
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/user/login";
        }
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/list")
    public String getUserList(Model model, Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        model.addAttribute("users", userService.getAllUsers());
        return "user/user-list";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model, Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        User user = userService.getAllUsers().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
        if (user == null) {
            model.addAttribute("message", "User not found.");
            return "redirect:/user/list";
        }
        model.addAttribute("user", user);
        return "user/edit-user";
    }

    @PostMapping("/edit/{id}")
    public String editUser(
            @PathVariable Long id,
            @ModelAttribute("user") User user,
            @ModelAttribute("adminRole") String adminRole,
            @ModelAttribute("department") String department,
            @ModelAttribute("active") Boolean active,
            @ModelAttribute("weddingDate") String weddingDate,
            @ModelAttribute("partnerName") String partnerName,
            @ModelAttribute("budget") String budgetStr,
            @ModelAttribute("guestCount") String guestCountStr, // Changed to String
            @ModelAttribute("venue") String venue,
            Model model,
            Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        Double budget = null;
        Integer guestCount = null;
        try {
            if (budgetStr != null && !budgetStr.isEmpty()) {
                budget = Double.parseDouble(budgetStr);
            }
            if (guestCountStr != null && !guestCountStr.isEmpty()) {
                guestCount = Integer.parseInt(guestCountStr); // Convert String to Integer
            }
        } catch (NumberFormatException e) {
            model.addAttribute("message", "Invalid budget or guest count format. Please enter valid numbers.");
            return "user/edit-user";
        }
        user.setId(id);
        userService.updateUser(user, adminRole, department, active, weddingDate, partnerName, budget, guestCount, venue);
        model.addAttribute("message", "User updated successfully!");
        return "redirect:/user/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, Model model, Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        userService.deleteUser(id);
        model.addAttribute("message", "User deleted successfully!");
        return "redirect:/user/list";
    }
}