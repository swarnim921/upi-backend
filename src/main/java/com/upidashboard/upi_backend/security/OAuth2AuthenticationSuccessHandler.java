package com.upidashboard.upi_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upidashboard.upi_backend.dto.AuthResponse;
import com.upidashboard.upi_backend.service.GoogleOAuth2Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final GoogleOAuth2Service googleOAuth2Service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        
        try {
            AuthResponse authResponse = googleOAuth2Service.processOAuth2User(oauth2User);
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            objectMapper.writeValue(response.getWriter(), authResponse);
            
            String email = oauth2User.getAttribute("email");
            log.info("OAuth2 authentication successful for user: {}", email);
        } catch (Exception e) {
            log.error("Error processing OAuth2 user", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication failed\"}");
        }
    }
}

