package com.upidashboard.upi_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * Webhook payload sent by Razorpay for payment events.
 * Events include: payment.captured, payment.failed, refund.created, etc.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RazorpayWebhookPayload {

    private String event; // e.g., "payment.captured", "payment.failed"
    private String entity = "event";

    @JsonProperty("account_id")
    private String accountId;

    private boolean contains;

    @JsonProperty("created_at")
    private long createdAt;

    private Payload payload;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        private PaymentEntity payment;
        private OrderEntity order;
        private RefundEntity refund;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentEntity {
        private Payment entity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderEntity {
        private Order entity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RefundEntity {
        private Refund entity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payment {
        private String id; // payment_id
        private String entity;
        private long amount; // in paise
        private String currency;
        private String status; // "captured", "failed", "authorized"

        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("invoice_id")
        private String invoiceId;

        private boolean international;
        private String method; // "upi", "card", "netbanking", etc.

        @JsonProperty("amount_refunded")
        private long amountRefunded;

        @JsonProperty("refund_status")
        private String refundStatus;

        private boolean captured;
        private String description;

        @JsonProperty("card_id")
        private String cardId;

        private String bank;
        private String wallet;
        private String vpa; // UPI VPA
        private String email;
        private String contact;

        @JsonProperty("fee")
        private long fee; // Razorpay fee in paise

        @JsonProperty("tax")
        private long tax;

        @JsonProperty("error_code")
        private String errorCode;

        @JsonProperty("error_description")
        private String errorDescription;

        @JsonProperty("error_source")
        private String errorSource;

        @JsonProperty("error_step")
        private String errorStep;

        @JsonProperty("error_reason")
        private String errorReason;

        private Map<String, String> notes;

        @JsonProperty("created_at")
        private long createdAt;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Order {
        private String id;
        private String entity;
        private long amount;

        @JsonProperty("amount_paid")
        private long amountPaid;

        @JsonProperty("amount_due")
        private long amountDue;

        private String currency;
        private String receipt;
        private String status; // "created", "attempted", "paid"

        private Map<String, String> notes;

        @JsonProperty("created_at")
        private long createdAt;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Refund {
        private String id;
        private String entity;
        private long amount;
        private String currency;

        @JsonProperty("payment_id")
        private String paymentId;

        private String status; // "processed", "pending", "failed"
        private String speed;

        @JsonProperty("created_at")
        private long createdAt;
    }
}
