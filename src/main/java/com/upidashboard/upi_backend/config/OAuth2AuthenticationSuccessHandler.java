package com.upidashboard.upi_backend.config;

import com.upidashboard.upi_backend.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseCookie;

import java.io.IOException;
import java.util.Collections;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Value("${app.frontend.url}")
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

        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");

            User userDetails = new User(
                    email,
                    "",
                    Collections.emptyList());

            token = jwtService.generateToken(userDetails);
        } else {
            User user = (User) authentication.getPrincipal();
            token = jwtService.generateToken(user);
        }

        // 🔐 Create secure cross-site cookie
        ResponseCookie jwtCookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(true) // REQUIRED for SameSite=None
                .sameSite("None") // REQUIRED for CloudFront → CloudFront
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .build();

        response.addHeader("Set-Cookie", jwtCookie.toString());

        // ✅ Redirect WITHOUT token in URL
        response.sendRedirect(frontendUrl + "/oauth-success");
    }
}
