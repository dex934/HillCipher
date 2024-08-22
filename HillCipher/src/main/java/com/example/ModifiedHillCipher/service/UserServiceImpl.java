package com.example.ModifiedHillCipher.service;

import com.example.ModifiedHillCipher.model.User;
import com.example.ModifiedHillCipher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {
        // You might want to add more validation or checks here
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false); // Set verified to false initially
        User savedUser = userRepository.save(user);
        sendVerificationEmail(savedUser);
        return savedUser;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void sendVerificationEmail(User user) {
        String otp = generateOTP();
        user.setOtp(otp);
        userRepository.save(user); // Save the OTP to the database

        String recipientAddress = user.getEmail();
        String subject = "OTP Verification";
        String message = "Your OTP for email verification is: " + otp;

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(emailSender);
            helper.setTo(recipientAddress);
            helper.setSubject(subject);
            helper.setText(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle the exception properly
        }

        javaMailSender.send(mimeMessage);
    }

    @Override
    public void verifyEmail(String token) {
        // Not needed for OTP-based verification
    }

    @Override
    public boolean verifyOTP(String email, String otp) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getOtp() != null && user.getOtp().equals(otp)) {
            user.setVerified(true); // Mark the user as verified
            userRepository.save(user);
            return true; // OTP verification successful
        }
        return false; // OTP verification failed
    }


    // Method to generate OTP
    private String generateOTP() {
        // Generate a 6-digit random OTP
        int otp = (int) ((Math.random() * (999999 - 100000)) + 100000);
        return String.valueOf(otp);
    }
}
