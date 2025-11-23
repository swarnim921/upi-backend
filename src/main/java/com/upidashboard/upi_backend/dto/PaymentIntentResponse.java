package com.upidashboard.upi_backend.dto;

import com.upidashboard.upi_backend.model.Transaction;
import com.upidashboard.upi_backend.model.TransactionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentIntentResponse {
    private String intentId;
    private String clientSecret;
    private TransactionStatus status;
    private String nextAction;
    private Transaction transaction;
}


