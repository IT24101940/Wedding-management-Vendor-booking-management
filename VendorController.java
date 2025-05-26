package com.dreamydayplanner.controller;
import com.dreamydayplanner.model.Vendor;
import com.dreamydayplanner.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vendor")
public class VendorController {
    @Autowired
    private VendorService vendorService;

    // Vendor Listing Page (User)
    @GetMapping("/list")
    public String getVendorList(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("vendors", vendorService.getAllVendors());
        return "vendor/vendor-list";
    }

    // Vendor Search Page (User)
    @GetMapping("/search")
    public String searchVendors(@RequestParam(value = "query", required = false) String query, Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("vendors", vendorService.searchVendors(query));
        model.addAttribute("query", query);
        return "vendor/vendor-search";
    }

    // Vendor Addition Form (Admin or User)
    @GetMapping("/add")
    public String showAddVendorForm(Model model, Authentication auth) {
        if (auth == null || (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) &&
                !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")))) {
            return "redirect:/user/login";
        }
        model.addAttribute("vendor", new Vendor());
        return "vendor/add-vendor";
    }

    @PostMapping("/add")
    public String addVendor(@ModelAttribute("vendor") Vendor vendor, Model model, Authentication auth) {
        if (auth == null || (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) &&
                !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")))) {
            return "redirect:/user/login";
        }
        vendorService.addVendor(vendor);
        model.addAttribute("message", "Vendor added successfully!");
        return "redirect:/vendor/list";
    }

    // Vendor Edit Page (Admin)
    @GetMapping("/edit/{id}")
    public String showEditVendorForm(@PathVariable Long id, Model model, Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        Vendor vendor = vendorService.findById(id);
        if (vendor == null) {
            model.addAttribute("message", "Vendor not found.");
            return "redirect:/vendor/list";
        }
        model.addAttribute("vendor", vendor);
        return "vendor/edit-vendor";
    }

    @PostMapping("/edit/{id}")
    public String editVendor(@PathVariable Long id, @ModelAttribute("vendor") Vendor updatedVendor, Model model, Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        try {
            updatedVendor.setId(id);
            vendorService.updateVendor(updatedVendor);
            model.addAttribute("message", "Vendor updated successfully!");
            return "redirect:/vendor/list";
        }catch (Exception e) {
            model.addAttribute("message", "Error updating vendor: " + e.getMessage());
            return "vendor/edit-vendor";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteVendor(@PathVariable Long id, Model model, Authentication auth) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/user/login";
        }
        vendorService.deleteVendor(id);
        model.addAttribute("message", "Vendor deleted successfully!");
        return "redirect:/vendor/list";
    }
}