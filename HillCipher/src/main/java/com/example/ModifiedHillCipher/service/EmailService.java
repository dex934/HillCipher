// EmailService.java
package com.example.ModifiedHillCipher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOTPEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("OTP Verification");
        message.setText("Dear User,\n\nThank you for choosing our platform. Your One-Time Passcode (OTP) for verification is: " + otp + "\n\nPlease enter this OTP to complete your registration or login process.\n\nIf you did not request this OTP, please ignore this message.\n\nBest regards,\nThe Hill Cipher Team");
        mailSender.send(message);
    }
}
