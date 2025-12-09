package com.upidashboard.upi_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for verifying Razorpay payment signature.
 * Frontend sends this after successful payment on Razorpay Checkout.
 */
@Data
public class RazorpayPaymentVerificationRequest {

    @NotBlank(message = "Razorpay order ID is required")
    private String razorpayOrderId;

    @NotBlank(message = "Razorpay payment ID is required")
    private String razorpayPaymentId;

    @NotBlank(message = "Razorpay signature is required")
    private String razorpaySignature;
}
