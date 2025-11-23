
package com.upidashboard.upi_backend.controller;

import com.upidashboard.upi_backend.dto.AuthRequest;
import com.upidashboard.upi_backend.dto.AuthResponse;
import com.upidashboard.upi_backend.dto.RegisterRequest;
import com.upidashboard.upi_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/google")
    public ResponseEntity<Map<String, String>> googleAuth() {
        // Spring Security OAuth2 automatically handles /oauth2/authorization/google
        // This endpoint provides the URL for frontend to redirect to
        return ResponseEntity.ok(Map.of(
                "authorizationUrl", "/oauth2/authorization/google",
                "message", "Redirect to /oauth2/authorization/google to initiate Google OAuth2 login"));
    }
}
