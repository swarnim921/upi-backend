package com.upidashboard.upi_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefundRequest {

    @NotBlank(message = "Payment intent ID is required")
    private String intentId;

    @Min(value = 0, message = "Refund amount cannot be negative")
    private double amount = 0;

    private String reason;
}
