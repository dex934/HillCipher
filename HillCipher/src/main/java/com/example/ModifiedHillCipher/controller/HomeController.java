package com.example.ModifiedHillCipher.controller;

import com.example.ModifiedHillCipher.model.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String homePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            if (!userDetails.getUser().isVerified()) {
                return "redirect:/unverified";
            }
            // Add any necessary model attributes
            model.addAttribute("user", userDetails.getUser());
            return "home"; // Return the name of the Thymeleaf template to render
        } else {
            // Handle the case where the principal is not an instance of CustomUserDetails
            return "redirect:/login";
        }
    }
}
