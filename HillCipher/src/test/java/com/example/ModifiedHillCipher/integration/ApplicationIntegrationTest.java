package com.example.ModifiedHillCipher.integration;

import com.example.ModifiedHillCipher.model.User;
import com.example.ModifiedHillCipher.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testUserRegistrationAndRetrieval() {
        User user = new User();
        user.setUsername("integrationTestUser");
        user.setEmail("test@integration.com");
        user.setPassword(passwordEncoder.encode("test123"));

        User savedUser = userRepository.save(user);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();

        // Clean up for the sake of this example
        userRepository.delete(savedUser);
    }
}
