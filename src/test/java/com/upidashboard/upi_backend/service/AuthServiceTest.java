package com.upidashboard.upi_backend.service;

import com.upidashboard.upi_backend.dto.AuthRequest;
import com.upidashboard.upi_backend.dto.AuthResponse;
import com.upidashboard.upi_backend.dto.RegisterRequest;
import com.upidashboard.upi_backend.model.Role;
import com.upidashboard.upi_backend.model.User;
import com.upidashboard.upi_backend.repository.UserRepository;
import com.upidashboard.upi_backend.security.CustomUserDetailsService;
import com.upidashboard.upi_backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user123")
                .name("John Doe")
                .email("john@example.com")
                .upiId("john@upi")
                .password("encodedPassword")
                .provider("local")
                .roles(Set.of(Role.USER))
                .balance(1000.0)
                .build();

        testUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username("john@example.com")
                .password("encodedPassword")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void register_ShouldCreateNewUser_WhenEmailNotExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setUpiId("john@upi");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(testUserDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("test-jwt-token");
        when(jwtService.getExpirationMinutes()).thenReturn(60L);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertNotNull(response.getUser());
        assertEquals("john@example.com", response.getUser().getEmail());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(testUserDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("test-jwt-token");
        when(jwtService.getExpirationMinutes()).thenReturn(60L);

        // Act
        AuthResponse response = authService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertNotNull(response.getUser());
        assertEquals("john@example.com", response.getUser().getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> authService.authenticate(request));
    }
}

