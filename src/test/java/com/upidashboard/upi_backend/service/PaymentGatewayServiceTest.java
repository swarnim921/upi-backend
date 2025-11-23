package com.upidashboard.upi_backend.service;

import com.upidashboard.upi_backend.dto.PaymentConfirmationRequest;
import com.upidashboard.upi_backend.dto.PaymentIntentRequest;
import com.upidashboard.upi_backend.dto.PaymentIntentResponse;
import com.upidashboard.upi_backend.dto.RefundRequest;
import com.upidashboard.upi_backend.model.Transaction;
import com.upidashboard.upi_backend.model.TransactionStatus;
import com.upidashboard.upi_backend.model.User;
import com.upidashboard.upi_backend.repository.TransactionRepository;
import com.upidashboard.upi_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentGatewayService paymentGatewayService;

    private User testUser;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user123")
                .name("John Doe")
                .email("john@example.com")
                .upiId("john@upi")
                .balance(1000.0)
                .build();

        testTransaction = Transaction.builder()
                .id("txn123")
                .intentId("pi_123456789012345678901234")
                .clientSecret("pi_secret_abc123")
                .userId("user123")
                .senderUpiId("john@upi")
                .receiverUpiId("merchant@upi")
                .amount(500.0)
                .currency("INR")
                .status(TransactionStatus.REQUIRES_CONFIRMATION)
                .build();
    }

    @Test
    void createIntent_ShouldCreateTransaction_WhenRequestIsValid() {
        // Arrange
        PaymentIntentRequest request = new PaymentIntentRequest();
        request.setAmount(500.0);
        request.setCurrency("INR");
        request.setSenderUpiId("john@upi");
        request.setReceiverUpiId("merchant@upi");
        request.setDescription("Test payment");

        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        PaymentIntentResponse response = paymentGatewayService.createIntent(request, testUser);

        // Assert
        assertNotNull(response);
        assertEquals(testTransaction.getIntentId(), response.getIntentId());
        assertEquals(TransactionStatus.REQUIRES_CONFIRMATION, response.getStatus());
        assertEquals("upi_collect_request", response.getNextAction());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void confirmIntent_ShouldSucceed_WhenOtpIsValid() {
        // Arrange
        PaymentConfirmationRequest request = new PaymentConfirmationRequest();
        request.setIntentId("pi_123456789012345678901234");
        request.setOtp("123456");

        when(transactionRepository.findByIntentId(request.getIntentId()))
                .thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        PaymentIntentResponse response = paymentGatewayService.confirmIntent(request, testUser);

        // Assert
        assertNotNull(response);
        assertEquals(TransactionStatus.SUCCEEDED, response.getStatus());
        assertEquals("none", response.getNextAction());
        verify(transactionRepository).save(any(Transaction.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void confirmIntent_ShouldFail_WhenOtpIsInvalid() {
        // Arrange
        PaymentConfirmationRequest request = new PaymentConfirmationRequest();
        request.setIntentId("pi_123456789012345678901234");
        request.setOtp("invalid");

        when(transactionRepository.findByIntentId(request.getIntentId()))
                .thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        PaymentIntentResponse response = paymentGatewayService.confirmIntent(request, testUser);

        // Assert
        assertNotNull(response);
        assertEquals(TransactionStatus.FAILED, response.getStatus());
        assertEquals("retry", response.getNextAction());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void confirmIntent_ShouldThrowException_WhenIntentNotFound() {
        // Arrange
        PaymentConfirmationRequest request = new PaymentConfirmationRequest();
        request.setIntentId("invalid_intent");

        when(transactionRepository.findByIntentId(request.getIntentId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, 
                () -> paymentGatewayService.confirmIntent(request, testUser));
    }

    @Test
    void confirmIntent_ShouldThrowException_WhenUserNotOwner() {
        // Arrange
        User otherUser = User.builder().id("otherUser").build();
        PaymentConfirmationRequest request = new PaymentConfirmationRequest();
        request.setIntentId("pi_123456789012345678901234");
        request.setOtp("123456");

        when(transactionRepository.findByIntentId(request.getIntentId()))
                .thenReturn(Optional.of(testTransaction));

        // Act & Assert
        assertThrows(AccessDeniedException.class, 
                () -> paymentGatewayService.confirmIntent(request, otherUser));
    }

    @Test
    void refundIntent_ShouldSucceed_WhenTransactionIsSucceeded() {
        // Arrange
        testTransaction.setStatus(TransactionStatus.SUCCEEDED);
        RefundRequest request = new RefundRequest();
        request.setIntentId("pi_123456789012345678901234");
        request.setAmount(500.0);
        request.setReason("Test refund");

        when(transactionRepository.findByIntentId(request.getIntentId()))
                .thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        var response = paymentGatewayService.refundIntent(request, testUser);

        // Assert
        assertNotNull(response);
        assertEquals(TransactionStatus.REFUNDED, response.getStatus());
        assertEquals(500.0, response.getRefundedAmount());
        verify(transactionRepository).save(any(Transaction.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void refundIntent_ShouldThrowException_WhenTransactionNotSucceeded() {
        // Arrange
        RefundRequest request = new RefundRequest();
        request.setIntentId("pi_123456789012345678901234");

        when(transactionRepository.findByIntentId(request.getIntentId()))
                .thenReturn(Optional.of(testTransaction));

        // Act & Assert
        assertThrows(ResponseStatusException.class, 
                () -> paymentGatewayService.refundIntent(request, testUser));
    }
}

