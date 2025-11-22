package com.upidashboard.upi_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentConfirmationRequest {

    @NotBlank(message = "Payment intent ID is required")
    private String intentId;

    @NotBlank(message = "OTP is required")
    private String otp;

    private String deviceFingerprint;
}
