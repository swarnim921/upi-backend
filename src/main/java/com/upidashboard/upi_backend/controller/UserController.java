package com.upidashboard.upi_backend.controller;

import com.upidashboard.upi_backend.dto.UserProfile;
import com.upidashboard.upi_backend.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfile getCurrentUser(Authentication authentication) {
        return userService.getProfileByEmail(authentication.getName());
    }

    @GetMapping("/wallet")
    public com.upidashboard.upi_backend.dto.WalletDto getWallet(Authentication authentication) {
        return userService.getWallet(authentication.getName());
    }

    @GetMapping("/{upiId}")
    public UserProfile getUserByUpiId(@PathVariable String upiId) {
        return userService.getUserByUpiId(upiId);
    }

    @GetMapping
    public List<UserProfile> getAllUsers() {
        return userService.getAllUserProfiles();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }
}