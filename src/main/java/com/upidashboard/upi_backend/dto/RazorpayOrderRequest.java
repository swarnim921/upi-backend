package com.upidashboard.upi_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * Request DTO for creating a Razorpay order.
 * Frontend sends this to initiate payment flow.
 */
@Data
public class RazorpayOrderRequest {

    @Min(value = 1, message = "Amount must be greater than zero")
    private long amount; // Amount in paise (e.g., 50000 = ₹500)

    @NotBlank(message = "Currency is required")
    private String currency = "INR";

    private String receipt; // Optional unique receipt id for your reference

    private Map<String, String> notes; // Optional metadata

    private boolean partialPayment = false; // Allow partial payment
}
