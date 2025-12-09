package com.upidashboard.upi_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

/**
 * Configuration for Razorpay API credentials.
 * Values are loaded from application.properties or environment variables.
 * 
 * IMPORTANT: Get these credentials from Group 3 (Razorpay API team).
 * - Test Mode: Use test credentials for development
 * - Live Mode: Use live credentials for production
 */
@Configuration
@Getter
public class RazorpayConfig {

    @Value("${razorpay.key-id:}")
    private String keyId;

    @Value("${razorpay.key-secret:}")
    private String keySecret;

    @Value("${razorpay.webhook-secret:}")
    private String webhookSecret;

    /**
     * Check if Razorpay is properly configured.
     * Use this before making API calls.
     */
    public boolean isConfigured() {
        return keyId != null && !keyId.isEmpty()
                && keySecret != null && !keySecret.isEmpty();
    }
}
