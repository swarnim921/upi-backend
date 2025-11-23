package com.upidashboard.upi_backend.controller;

import com.upidashboard.upi_backend.dto.PaymentConfirmationRequest;
import com.upidashboard.upi_backend.dto.PaymentIntentRequest;
import com.upidashboard.upi_backend.dto.PaymentIntentResponse;
import com.upidashboard.upi_backend.dto.RefundRequest;
import com.upidashboard.upi_backend.dto.RefundResponse;
import com.upidashboard.upi_backend.service.PaymentGatewayService;
import com.upidashboard.upi_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentGatewayService paymentGatewayService;
    private final UserService userService;

    @PostMapping("/intent")
    public ResponseEntity<PaymentIntentResponse> createIntent(
        @Valid @RequestBody PaymentIntentRequest request,
        Authentication authentication
    ) {
        var user = userService.getEntityByEmail(authentication.getName());
        return ResponseEntity.ok(paymentGatewayService.createIntent(request, user));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentIntentResponse> confirmIntent(
        @Valid @RequestBody PaymentConfirmationRequest request,
        Authentication authentication
    ) {
        var user = userService.getEntityByEmail(authentication.getName());
        return ResponseEntity.ok(paymentGatewayService.confirmIntent(request, user));
    }

    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> refundIntent(
        @Valid @RequestBody RefundRequest request,
        Authentication authentication
    ) {
        var user = userService.getEntityByEmail(authentication.getName());
        return ResponseEntity.ok(paymentGatewayService.refundIntent(request, user));
    }
}

