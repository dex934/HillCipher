package com.example.ModifiedHillCipher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UnverifiedController {

    @GetMapping("/unverified")
    public String showUnverifiedPage() {
        return "unverified"; // Assuming you have a template named "unverified.html"
    }
}

