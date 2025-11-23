package com.upidashboard.upi_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendPaymentRequest {
    @NotBlank(message = "Recipient UPI ID is required")
    private String recipientUpiId;

    @Min(value = 1, message = "Amount must be greater than zero")
    private double amount;

    private String description;
}
