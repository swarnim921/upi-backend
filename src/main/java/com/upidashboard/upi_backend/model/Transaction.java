package com.upidashboard.upi_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
<<<<<<< HEAD
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
=======
import java.time.LocalDateTime;

>>>>>>> a9fde5b945b9b33778f4479bfb2c9c257e0e31fa
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
<<<<<<< HEAD
    private String sender;
    private String receiver;
    private Double amount;
    private LocalDateTime date;
}
=======
    private String senderUpiId;
    private String receiverUpiId;
    private double amount;
    private String status; // "SUCCESS", "FAILED", "PENDING"
    private LocalDateTime timestamp = LocalDateTime.now();

    // Getters and Setters
}
>>>>>>> a9fde5b945b9b33778f4479bfb2c9c257e0e31fa
