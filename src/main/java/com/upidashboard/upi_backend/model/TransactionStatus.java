package com.upidashboard.upi_backend.model;

public enum TransactionStatus {
    REQUIRES_PAYMENT_METHOD,
    REQUIRES_CONFIRMATION,
    PROCESSING,
    SUCCEEDED,
    FAILED,
    REFUNDED
}


