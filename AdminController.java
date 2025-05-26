package com.dreamydayplanner.controller;

import com.dreamydayplanner.model.AdminUser;
import com.dreamydayplanner.model.RegularUser;
import com.dreamydayplanner.model.User;
import com.dreamydayplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        model.addAttribute("usersCount", userService.getAllUsers().size());
        return "admin/admin-dashboard";
    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/manage-user";
    }

    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        userService.deleteUser(id);
        model.addAttribute("message", "User deleted successfully!");
        return "redirect:/admin/manage-users";
    }

    @GetMapping("/edit-user/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getAllUsers().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
        if (user == null) {
            model.addAttribute("message", "User not found.");
            return "redirect:/admin/manage-users";
        }
        model.addAttribute("user", user);
        return "admin/edit-user";
    }

    @PostMapping("/edit-user/{id}")
    public String editUser(
            @PathVariable Long id,
            @ModelAttribute("user") User updatedUser,
            @RequestParam(required = false) String adminRole,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String weddingDate,
            @RequestParam(required = false) String partnerName,
            @RequestParam(required = false) Double budget,
            @RequestParam(required = false) Integer guestCount,
            @RequestParam(required = false) String venue,
            Model model) {
        User user = userService.getAllUsers().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
        if (user == null) {
            model.addAttribute("message", "User not found.");
            return "redirect:/admin/manage-users";
        }
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        userService.updateUser(user, adminRole, department, active != null ? active : false, weddingDate, partnerName, budget, guestCount, venue);
        model.addAttribute("message", "User updated successfully!");
        return "redirect:/admin/manage-users";
    }
}