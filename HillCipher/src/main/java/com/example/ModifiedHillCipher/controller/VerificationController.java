package com.example.ModifiedHillCipher.controller;

import com.example.ModifiedHillCipher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VerificationController {

    @Autowired
    private UserService userService;

    @PostMapping("/verifyOTP")  // Change to @PostMapping as the form submits using POST method
    public String verifyOTP(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model) {
        boolean success = userService.verifyOTP(email, otp);
        if (success) {
            // If OTP verification is successful, redirect to a success page
            return "verification_success";
        } else {
            // If OTP verification fails, redirect to a failure page
            return "verification_failed";
        }
    }
}
