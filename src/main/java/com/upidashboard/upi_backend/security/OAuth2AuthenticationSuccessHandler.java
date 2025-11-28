package com.upidashboard.upi_backend.security;

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

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        try {
            AuthResponse authResponse = googleOAuth2Service.processOAuth2User(oauth2User);

            // Redirect to Frontend with Token
            // AuthResponse contains UserProfile in 'user' field
            String targetUrl = frontendUrl + "/oauth/callback" +
                    "?token=" + authResponse.getToken() +
                    "&id=" + authResponse.getUser().getId() +
                    "&username=" + (authResponse.getUser().getName() != null ? authResponse.getUser().getName() : "") +
                    "&email=" + authResponse.getUser().getEmail() +
                    "&roles=" + authResponse.getUser().getRoles().stream().map(Enum::name).reduce((a, b) -> a + "," + b)
                            .orElse("");

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("Error processing OAuth2 user", e);
            // Redirect to login with error
            getRedirectStrategy().sendRedirect(request, response,
                    frontendUrl + "/auth?error=authentication_failed");
        }
    }
}
