package com.upidashboard.upi_backend.dto;

import com.upidashboard.upi_backend.model.TransactionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundResponse {
    private String intentId;
    private double refundedAmount;
    private TransactionStatus status;
    private String message;
}


