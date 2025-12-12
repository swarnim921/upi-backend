package com.upidashboard.upi_backend.config;

import com.upidashboard.upi_backend.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url}")
    private String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        String token;

        // Check if the user is OAuth2User (Google login)
        if (authentication
                .getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");
            // You can also get name, picture, etc.
            // Generate JWT for this email
            token = jwtService.generateToken(email);
        } else {
            // Standard login
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication
                    .getPrincipal();
            token = jwtService.generateToken(user);
        }

        // Redirect to frontend with JWT
        String redirectUrl = frontendUrl + "/oauth-success?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}
