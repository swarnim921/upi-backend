package com.upidashboard.upi_backend.service;

import com.upidashboard.upi_backend.dto.PaymentConfirmationRequest;
import com.upidashboard.upi_backend.dto.PaymentIntentRequest;
import com.upidashboard.upi_backend.dto.PaymentIntentResponse;
import com.upidashboard.upi_backend.dto.RefundRequest;
import com.upidashboard.upi_backend.dto.RefundResponse;
import com.upidashboard.upi_backend.model.Transaction;
import com.upidashboard.upi_backend.model.TransactionStatus;
import com.upidashboard.upi_backend.model.User;
import com.upidashboard.upi_backend.repository.TransactionRepository;
import com.upidashboard.upi_backend.repository.UserRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public PaymentIntentResponse createIntent(PaymentIntentRequest request, User user) {
        String currency = StringUtils.hasText(request.getCurrency()) ? request.getCurrency() : "INR";

        Transaction transaction = Transaction.builder()
                .intentId(generateIntentId())
                .clientSecret(generateClientSecret())
                .userId(user.getId())
                .senderUpiId(request.getSenderUpiId())
                .receiverUpiId(request.getReceiverUpiId())
                .amount(request.getAmount())
                .currency(currency.toUpperCase(Locale.ROOT))
                .status(TransactionStatus.REQUIRES_CONFIRMATION)
                .description(request.getDescription())
                .metadata(request.getMetadata() == null ? new java.util.HashMap<>() : request.getMetadata())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return buildResponse(saved, "upi_collect_request");
    }

    public PaymentIntentResponse confirmIntent(PaymentConfirmationRequest request, User user) {
        Transaction transaction = transactionRepository.findByIntentId(request.getIntentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid intent id"));

        validateOwnership(transaction, user);

        if (!isOtpValid(request.getOtp())) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureCode("otp_invalid");
            transaction.setFailureMessage("The provided OTP is invalid");
            transaction.setUpdatedAt(LocalDateTime.now());
            Transaction failed = transactionRepository.save(transaction);
            return buildResponse(failed, "retry");
        }

        transaction.setStatus(TransactionStatus.SUCCEEDED);
        transaction.setUtr(generateUtr());
        transaction.setFailureCode(null);
        transaction.setFailureMessage(null);
        transaction.setUpdatedAt(LocalDateTime.now());
        Transaction succeeded = transactionRepository.save(transaction);

        user.setBalance(Math.max(0, user.getBalance() - transaction.getAmount()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return buildResponse(succeeded, "none");
    }

    public RefundResponse refundIntent(RefundRequest request, User user) {
        Transaction transaction = transactionRepository.findByIntentId(request.getIntentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid intent id"));

        validateOwnership(transaction, user);

        if (transaction.getStatus() != TransactionStatus.SUCCEEDED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only succeeded payments can be refunded");
        }

        double refundAmount = request.getAmount() == 0 ? transaction.getAmount() : request.getAmount();
        if (refundAmount > transaction.getAmount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refund amount cannot exceed original amount");
        }

        transaction.setStatus(TransactionStatus.REFUNDED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        user.setBalance(user.getBalance() + refundAmount);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return RefundResponse.builder()
                .intentId(transaction.getIntentId())
                .refundedAmount(refundAmount)
                .status(transaction.getStatus())
                .message(request.getReason() != null ? request.getReason() : "Refund processed")
                .build();
    }

    public PaymentIntentResponse processDirectPayment(com.upidashboard.upi_backend.dto.SendPaymentRequest request,
            User sender) {
        // 1. Create Intent
        String currency = "INR";
        Transaction transaction = Transaction.builder()
                .intentId(generateIntentId())
                .clientSecret(generateClientSecret())
                .userId(sender.getId())
                .senderUpiId(sender.getUpiId()) // Assuming sender has UPI ID
                .receiverUpiId(request.getRecipientUpiId())
                .amount(request.getAmount())
                .currency(currency)
                .status(TransactionStatus.SUCCEEDED) // Direct success
                .description(request.getDescription())
                .metadata(new java.util.HashMap<>())
                .utr(generateUtr())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 2. Update Sender Balance
        if (sender.getBalance() < request.getAmount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }
        sender.setBalance(sender.getBalance() - request.getAmount());
        sender.setUpdatedAt(LocalDateTime.now());
        userRepository.save(sender);

        // 3. Update Receiver Balance (if exists in our system)
        userRepository.findByUpiId(request.getRecipientUpiId()).ifPresent(receiver -> {
            receiver.setBalance(receiver.getBalance() + request.getAmount());
            receiver.setUpdatedAt(LocalDateTime.now());
            userRepository.save(receiver);
        });

        // 4. Save Transaction
        Transaction saved = transactionRepository.save(transaction);

        return buildResponse(saved, "none");
    }

    private void validateOwnership(Transaction transaction, User user) {
        if (!transaction.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot act on another user's payment intent");
        }
    }

    private PaymentIntentResponse buildResponse(Transaction transaction, String nextAction) {
        return PaymentIntentResponse.builder()
                .intentId(transaction.getIntentId())
                .clientSecret(transaction.getClientSecret())
                .status(transaction.getStatus())
                .nextAction(nextAction)
                .transaction(transaction)
                .build();
    }

    private boolean isOtpValid(String otp) {
        return otp != null && otp.matches("\\d{6}");
    }

    private String generateIntentId() {
        return "pi_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }

    private String generateClientSecret() {
        return "pi_secret_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String generateUtr() {
        return "UTR" + Math.abs(secureRandom.nextLong());
    }
}
