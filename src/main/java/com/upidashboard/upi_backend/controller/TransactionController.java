package com.upidashboard.upi_backend.controller;

import com.upidashboard.upi_backend.model.Transaction;
import com.upidashboard.upi_backend.service.TransactionService;
import com.upidashboard.upi_backend.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping
    public List<Transaction> getTransactionsForCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        var user = userService.getEntityByEmail(email);
        return transactionService.getTransactionsForUser(user.getId());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
}
