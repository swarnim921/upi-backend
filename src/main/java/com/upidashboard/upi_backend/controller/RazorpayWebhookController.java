package com.upidashboard.upi_backend.controller;

import com.upidashboard.upi_backend.config.RazorpayConfig;
import com.upidashboard.upi_backend.dto.RazorpayOrderRequest;
import com.upidashboard.upi_backend.dto.RazorpayOrderResponse;
import com.upidashboard.upi_backend.dto.RazorpayWebhookPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Controller for handling Razorpay webhook events.
 * 
 * SETUP REQUIRED:
 * 1. Get webhook secret from Razorpay Dashboard → Settings → Webhooks
 * 2. Add to application.properties: razorpay.webhook-secret=your_secret
 * 3. Configure webhook URL in Razorpay:
 * https://yourdomain.com/api/razorpay/webhook
 * 
 * Events to subscribe (ask Group 3):
 * - payment.authorized
 * - payment.captured
 * - payment.failed
 * - refund.created
 * - refund.processed
 */
@RestController
@RequestMapping("/api/razorpay")
@RequiredArgsConstructor
@Slf4j
public class RazorpayWebhookController {

    private final RazorpayConfig razorpayConfig;
    private final ObjectMapper objectMapper;

    /**
     * Create a Razorpay order.
     * This endpoint initializes a payment order with Razorpay.
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody RazorpayOrderRequest request) {
        log.info("Creating Razorpay order for amount: {} {}", request.getAmount(), request.getCurrency());

        // Validate Razorpay configuration
        if (!razorpayConfig.isConfigured()) {
            log.error("Razorpay is not configured. Missing key-id or key-secret.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Razorpay is not configured"));
        }

        try {
            // Initialize Razorpay client
            RazorpayClient razorpayClient = new RazorpayClient(
                    razorpayConfig.getKeyId(),
                    razorpayConfig.getKeySecret());

            // Create order request (amount must be in paise)
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount() * 100); // Convert to paise
            orderRequest.put("currency", request.getCurrency() != null ? request.getCurrency() : "INR");
            orderRequest.put("receipt", "order_" + System.currentTimeMillis());

            // Create order via Razorpay SDK
            Order order = razorpayClient.orders.create(orderRequest);

            // Build response
            RazorpayOrderResponse response = RazorpayOrderResponse.builder()
                    .orderId(order.get("id"))
                    .amount(Integer.parseInt(order.get("amount").toString()))
                    .currency(order.get("currency"))
                    .receipt(order.get("receipt"))
                    .status(order.get("status"))
                    .keyId(razorpayConfig.getKeyId())
                    .build();

            log.info("Razorpay order created successfully: {}", response.getOrderId());
            return ResponseEntity.ok(response);

        } catch (RazorpayException e) {
            log.error("Razorpay API error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Razorpay API error", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating Razorpay order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create order", "message", e.getMessage()));
        }
    }

    /**
     * Webhook endpoint for Razorpay events.
     * Razorpay sends POST requests here when payment events occur.
     */
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {

        log.info("Received Razorpay webhook");

        // Verify webhook signature (skip if webhook secret not configured)
        if (razorpayConfig.getWebhookSecret() != null && !razorpayConfig.getWebhookSecret().isEmpty()) {
            if (!verifyWebhookSignature(payload, signature)) {
                log.warn("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Invalid signature"));
            }
        }

        try {
            RazorpayWebhookPayload webhookPayload = objectMapper.readValue(payload, RazorpayWebhookPayload.class);
            String event = webhookPayload.getEvent();

            log.info("Processing Razorpay event: {}", event);

            switch (event) {
                case "payment.captured":
                    handlePaymentCaptured(webhookPayload);
                    break;
                case "payment.failed":
                    handlePaymentFailed(webhookPayload);
                    break;
                case "refund.created":
                case "refund.processed":
                    handleRefund(webhookPayload);
                    break;
                case "payment.authorized":
                    handlePaymentAuthorized(webhookPayload);
                    break;
                default:
                    log.info("Unhandled event type: {}", event);
            }

            return ResponseEntity.ok(Map.of("status", "ok"));

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * Handle successful payment capture.
     * TODO: Update transaction status, notify user, etc.
     */
    private void handlePaymentCaptured(RazorpayWebhookPayload payload) {
        var payment = payload.getPayload().getPayment().getEntity();
        log.info("Payment captured: {} for amount {} paise", payment.getId(), payment.getAmount());

        // TODO: Implement your business logic here
        // 1. Find transaction by orderId
        // 2. Update transaction status to SUCCEEDED
        // 3. Update user balance
        // 4. Send notification
    }

    /**
     * Handle failed payment.
     * TODO: Update transaction status with failure reason.
     */
    private void handlePaymentFailed(RazorpayWebhookPayload payload) {
        var payment = payload.getPayload().getPayment().getEntity();
        log.warn("Payment failed: {} - Error: {} - {}",
                payment.getId(),
                payment.getErrorCode(),
                payment.getErrorDescription());

        // TODO: Implement your business logic here
        // 1. Find transaction by orderId
        // 2. Update transaction status to FAILED
        // 3. Store error details
    }

    /**
     * Handle refund events.
     */
    private void handleRefund(RazorpayWebhookPayload payload) {
        var refund = payload.getPayload().getRefund().getEntity();
        log.info("Refund processed: {} for payment {} - Amount: {} paise",
                refund.getId(),
                refund.getPaymentId(),
                refund.getAmount());

        // TODO: Implement refund processing logic
    }

    /**
     * Handle payment authorization (before capture).
     */
    private void handlePaymentAuthorized(RazorpayWebhookPayload payload) {
        var payment = payload.getPayload().getPayment().getEntity();
        log.info("Payment authorized: {} for amount {} paise", payment.getId(), payment.getAmount());

        // TODO: Auto-capture if needed, or wait for manual capture
    }

    /**
     * Verify webhook signature using HMAC SHA256.
     */
    private boolean verifyWebhookSignature(String payload, String signature) {
        if (signature == null || signature.isEmpty()) {
            return false;
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    razorpayConfig.getWebhookSecret().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = bytesToHex(hash);

            return expectedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying signature: {}", e.getMessage());
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
