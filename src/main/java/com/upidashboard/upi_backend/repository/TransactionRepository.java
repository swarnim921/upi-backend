package com.upidashboard.upi_backend.repository;

import com.upidashboard.upi_backend.model.Transaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Optional<Transaction> findByIntentId(String intentId);
    List<Transaction> findByUserIdOrderByCreatedAtDesc(String userId);
}
