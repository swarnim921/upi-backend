package com.upidashboard.upi_backend.controller;

import com.upidashboard.upi_backend.dto.PaymentConfirmationRequest;
import com.upidashboard.upi_backend.dto.PaymentIntentRequest;
import com.upidashboard.upi_backend.dto.PaymentIntentResponse;
import com.upidashboard.upi_backend.dto.RefundRequest;
import com.upidashboard.upi_backend.dto.RefundResponse;
import com.upidashboard.upi_backend.service.PaymentGatewayService;
import com.upidashboard.upi_backend.service.UserService;
import com.upidashboard.upi_backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import com.upidashboard.upi_backend.model.Transaction;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentGatewayService paymentGatewayService;
    private final UserService userService;
    private final TransactionService transactionService;

    @PostMapping("/intent")
    public ResponseEntity<PaymentIntentResponse> createIntent(
            @Valid @RequestBody PaymentIntentRequest request,
            Authentication authentication) {
        var user = userService.getEntityByEmail(authentication.getName());
        return ResponseEntity.ok(paymentGatewayService.createIntent(request, user));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentIntentResponse> confirmIntent(
            @Valid @RequestBody PaymentConfirmationRequest request,
            Authentication authentication) {
        var user = userService.getEntityByEmail(authentication.getName());
        return ResponseEntity.ok(paymentGatewayService.confirmIntent(request, user));
    }

    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> refundIntent(
            @Valid @RequestBody RefundRequest request,
            Authentication authentication) {
        var user = userService.getEntityByEmail(authentication.getName());
        return ResponseEntity.ok(paymentGatewayService.refundIntent(request, user));
    }

    @PostMapping("/send")
    public ResponseEntity<PaymentIntentResponse> sendPayment(
            @Valid @RequestBody com.upidashboard.upi_backend.dto.SendPaymentRequest request,
            Authentication authentication) {
        var user = userService.getEntityByEmail(authentication.getName());
        return ResponseEntity.ok(paymentGatewayService.processDirectPayment(request, user));
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(Authentication authentication) {
        var user = userService.getEntityByEmail(authentication.getName());
        return transactionService.getTransactionsForUser(user.getId());
    }
}
