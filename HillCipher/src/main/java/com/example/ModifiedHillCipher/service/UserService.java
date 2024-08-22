package com.example.ModifiedHillCipher.service;

import com.example.ModifiedHillCipher.model.User;

public interface UserService {

    User registerUser(User user);

    User findByUsername(String username);

    User findByEmail(String email);

    void sendVerificationEmail(User user);

    void verifyEmail(String token);

    boolean verifyOTP(String email, String otp); // Method to verify OTP
}
