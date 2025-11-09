package com.upidashboard.upi_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.upidashboard.upi_backend.model.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
}
