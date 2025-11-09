package com.upidashboard.upi_backend.repository;

import com.upidashboard.upi_backend.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> { }
