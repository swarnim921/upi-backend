package com.upidashboard.upi_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class PaymentIntentRequest {

    @Min(value = 1, message = "Amount must be greater than zero")
    private double amount;

    @NotBlank
    private String currency = "INR";

    @NotBlank
    private String senderUpiId;

    @NotBlank
    private String receiverUpiId;

    private String description;

    private Map<String, Object> metadata = new HashMap<>();
}

