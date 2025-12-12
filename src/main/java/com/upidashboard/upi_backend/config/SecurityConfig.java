package com.upidashboard.upi_backend.config;

import com.upidashboard.upi_backend.security.JwtAuthenticationFilter;
import com.upidashboard.upi_backend.security.CustomUserDetailsService;
import com.upidashboard.upi_backend.config.OAuth2AuthenticationSuccessHandler;
import com.upidashboard.upi_backend.service.GoogleOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final CustomUserDetailsService userDetailsService;
        private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
        private final GoogleOAuth2Service googleOAuth2Service;

        @Value("${app.frontend.url}")
        private String frontendUrl;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/api/health",
                                                                "/actuator/health",
                                                                "/error",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/oauth2/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .redirectionEndpoint(
                                                                redir -> redir.baseUri("/api/auth/oauth2/callback/*") // accept
                                                                                                                      // Google
                                                                                                                      // callback
                                                )
                                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                                .userInfoEndpoint(
                                                                userInfo -> userInfo.userService(googleOAuth2Service)))
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList(
                                frontendUrl,
                                "http://localhost:8080",
                                "http://localhost:5173",
                                "http://16.112.4.208", // Frontend IP
                                "http://16.171.241.145" // Google OAuth Redirect IP
                ));
                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(Arrays.asList(
                                "Authorization", "Content-Type", "X-Requested-With"));
                configuration.setExposedHeaders(Arrays.asList("Authorization"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
