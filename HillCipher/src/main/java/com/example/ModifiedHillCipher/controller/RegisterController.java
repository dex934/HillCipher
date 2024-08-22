package com.example.ModifiedHillCipher.controller;

import com.example.ModifiedHillCipher.model.User;
import com.example.ModifiedHillCipher.service.EmailService;
import com.example.ModifiedHillCipher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(User user) {
        // Register the user and generate OTP
        User registeredUser = userService.registerUser(user);
        emailService.sendOTPEmail(registeredUser.getEmail(), registeredUser.getOtp());
        return "otp_verification"; // Redirect to the OTP verification page
    }
}
