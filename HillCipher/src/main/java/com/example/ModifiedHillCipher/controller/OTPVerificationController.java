package com.example.ModifiedHillCipher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OTPVerificationController {

    @GetMapping("/verifyOTP")
    public String showOTPVerificationPage() {
        return "otp_verification"; // Return the name of the Thymeleaf template for OTP verification page
    }
}
