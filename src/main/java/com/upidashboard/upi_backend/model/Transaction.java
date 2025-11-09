package com.upidashboard.upi_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String senderUpiId;
    private String receiverUpiId;
    private double amount;
    private String status; // "SUCCESS", "FAILED", "PENDING"
    private LocalDateTime timestamp = LocalDateTime.now();

    // Getters and Setters
}
