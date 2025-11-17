package com.upidashboard.upi_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentConfirmationRequest {

    @NotBlank
    private String intentId;

    @NotBlank
    private String otp;

    private String deviceFingerprint;
}


