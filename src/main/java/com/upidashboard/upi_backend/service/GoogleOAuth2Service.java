package com.upidashboard.upi_backend.service;

import com.upidashboard.upi_backend.dto.AuthResponse;
import com.upidashboard.upi_backend.dto.UserProfile;
import com.upidashboard.upi_backend.model.Role;
import com.upidashboard.upi_backend.model.User;
import com.upidashboard.upi_backend.repository.UserRepository;
import com.upidashboard.upi_backend.security.CustomUserDetailsService;
import com.upidashboard.upi_backend.security.JwtService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        return oauth2User;
    }

    @Transactional
    public AuthResponse processOAuth2User(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub"); // Google's unique user ID

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email not found in OAuth2 user attributes");
        }

        // Check if user exists by email
        User user = userRepository.findByEmail(email)
            .orElse(null);

        if (user != null) {
            // User exists - update provider info if needed
            if (user.getProvider() == null || !user.getProvider().equals("google")) {
                user.setProvider("google");
                user.setProviderId(providerId);
                user.setUpdatedAt(LocalDateTime.now());
                user = userRepository.save(user);
            }
        } else {
            // New user - create account
            String upiId = generateUpiId(email, name);
            user = User.builder()
                .name(name != null ? name : email.split("@")[0])
                .email(email)
                .upiId(upiId)
                .provider("google")
                .providerId(providerId)
                .roles(Set.of(Role.USER))
                .balance(0.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            user = userRepository.save(user);
            log.info("Created new user via Google OAuth2: {}", email);
        }

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

    private UserProfile toProfile(User user) {
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

    private String generateUpiId(String email, String name) {
        // Generate UPI ID from email or name
        String base = name != null && !name.isEmpty() 
            ? name.toLowerCase().replaceAll("[^a-z0-9]", "")
            : email.split("@")[0].toLowerCase();
        
        // Ensure uniqueness by checking if UPI ID exists
        String upiId = base + "@upi";
        int counter = 1;
        while (userRepository.findByUpiId(upiId).isPresent()) {
            upiId = base + counter + "@upi";
            counter++;
        }
        
        return upiId;
    }
}

