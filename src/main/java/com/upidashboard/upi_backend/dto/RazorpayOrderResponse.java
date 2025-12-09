package com.upidashboard.upi_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO returned after creating a Razorpay order.
 * Frontend uses this to open Razorpay Checkout.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderResponse {

    private String orderId; // Razorpay order_id (e.g., "order_DBJOWzybf0sJbb")
    private String keyId; // Razorpay Key ID for frontend
    private long amount; // Amount in paise
    private String currency;
    private String receipt;
    private String status; // "created", "attempted", "paid"
    private Map<String, String> notes;

    // Optional: prefill data for checkout
    private String prefillName;
    private String prefillEmail;
    private String prefillContact;
}
