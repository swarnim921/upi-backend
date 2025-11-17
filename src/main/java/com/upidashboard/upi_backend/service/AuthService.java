package com.upidashboard.upi_backend.service;

import com.upidashboard.upi_backend.dto.AuthRequest;
import com.upidashboard.upi_backend.dto.AuthResponse;
import com.upidashboard.upi_backend.dto.RegisterRequest;
import com.upidashboard.upi_backend.dto.UserProfile;
import com.upidashboard.upi_backend.model.Role;
import com.upidashboard.upi_backend.model.User;
import com.upidashboard.upi_backend.repository.UserRepository;
import com.upidashboard.upi_backend.security.CustomUserDetailsService;
import com.upidashboard.upi_backend.security.JwtService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .upiId(request.getUpiId())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(Set.of(Role.USER))
            .build();

        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);
        long expiresAt = Instant.now()
            .plus(jwtService.getExpirationMinutes(), ChronoUnit.MINUTES)
            .toEpochMilli();

        return AuthResponse.builder()
            .token(token)
            .expiresAt(expiresAt)
            .user(toProfile(user))
            .build();
    }

    public UserProfile toProfile(User user) {
        return UserProfile.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .upiId(user.getUpiId())
            .balance(user.getBalance())
            .roles(user.getRoles())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}


