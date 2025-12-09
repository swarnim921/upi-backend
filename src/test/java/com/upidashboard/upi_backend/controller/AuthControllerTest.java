package com.upidashboard.upi_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upidashboard.upi_backend.dto.AuthRequest;
import com.upidashboard.upi_backend.dto.AuthResponse;
import com.upidashboard.upi_backend.dto.RegisterRequest;
import com.upidashboard.upi_backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@org.springframework.context.annotation.Import({
                com.upidashboard.upi_backend.config.SecurityConfig.class,
                com.upidashboard.upi_backend.security.JwtAuthenticationFilter.class
})
@org.springframework.test.context.TestPropertySource(properties = {
                "spring.security.oauth2.client.registration.google.client-id=test-client-id",
                "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService authService;

        @MockBean
        private com.upidashboard.upi_backend.security.JwtService jwtService;

        @MockBean
        private com.upidashboard.upi_backend.security.CustomUserDetailsService customUserDetailsService;

        @MockBean
        private com.upidashboard.upi_backend.config.OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

        @MockBean
        private com.upidashboard.upi_backend.service.GoogleOAuth2Service googleOAuth2Service;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void register_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
                // Arrange
                RegisterRequest request = new RegisterRequest();
                request.setName("John Doe");
                request.setEmail("john@example.com");
                request.setUpiId("john@upi");
                request.setPassword("password123");

                AuthResponse response = AuthResponse.builder()
                                .token("test-jwt-token")
                                .expiresAt(System.currentTimeMillis() + 3600000)
                                .build();

                when(authService.register(any(RegisterRequest.class))).thenReturn(response);

                // Act & Assert
                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.token").value("test-jwt-token"));
        }

        @Test
        void login_ShouldReturnOk_WhenCredentialsAreValid() throws Exception {
                // Arrange
                AuthRequest request = new AuthRequest();
                request.setEmail("john@example.com");
                request.setPassword("password123");

                AuthResponse response = AuthResponse.builder()
                                .token("test-jwt-token")
                                .expiresAt(System.currentTimeMillis() + 3600000)
                                .build();

                when(authService.authenticate(any(AuthRequest.class))).thenReturn(response);

                // Act & Assert
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("test-jwt-token"));
        }
}
