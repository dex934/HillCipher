package com.example.ModifiedHillCipher.service;

import com.example.ModifiedHillCipher.model.CustomUserDetails;
import com.example.ModifiedHillCipher.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userService.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userService.findByEmail(usernameOrEmail);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
        }

        return new CustomUserDetails(user);
    }
}
