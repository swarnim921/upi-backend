package com.upidashboard.upi_backend.service;

import com.upidashboard.upi_backend.model.Transaction;
import com.upidashboard.upi_backend.repository.TransactionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsForUser(String userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction getByIntentId(String intentId) {
        return transactionRepository.findByIntentId(intentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid intent id"));
    }
}
