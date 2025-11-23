package com.upidashboard.upi_backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @GetMapping("/health")
    public String adminHealth() {
        return "Admin module is active and ready!";
    }
}
