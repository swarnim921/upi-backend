package com.upidashboard.upi_backend.config;

import com.upidashboard.upi_backend.model.*;
import com.upidashboard.upi_backend.repository.AdminRepository;
import com.upidashboard.upi_backend.repository.TransactionRepository;
import com.upidashboard.upi_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final TransactionRepository transactionRepository;
  private final PasswordEncoder passwordEncoder;

  @Bean
  CommandLineRunner initDatabase() {
    return args -> {
      // Clear existing data (optional - comment out if you don't want to clear)
      log.info("Starting database seeding...");

      // Seed Admin
      if (adminRepository.count() == 0) {
        Admin admin = Admin.builder()
            .name("Admin User")
            .email("admin@upidashboard.com")
            .password(passwordEncoder.encode("admin123"))
            .build();
        adminRepository.save(admin);
        log.info("Admin user created: {}", admin.getEmail());
      }

      // Seed Users
      if (userRepository.count() == 0) {
        User user1 = User.builder()
            .name("John Doe")
            .email("john.doe@example.com")
            .upiId("john@paytm")
            .password(passwordEncoder.encode("password123"))
            .roles(Set.of(Role.USER))
            .balance(10000.0)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        User user2 = User.builder()
            .name("Jane Smith")
            .email("jane.smith@example.com")
            .upiId("jane@phonepe")
            .password(passwordEncoder.encode("password123"))
            .roles(Set.of(Role.USER))
            .balance(15000.0)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        User user3 = User.builder()
            .name("Robert Johnson")
            .email("robert.j@example.com")
            .upiId("robert@gpay")
            .password(passwordEncoder.encode("password123"))
            .roles(Set.of(Role.USER))
            .balance(5000.0)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        User user4 = User.builder()
            .name("Alice Williams")
            .email("alice.w@example.com")
            .upiId("alice@upi")
            .password(passwordEncoder.encode("password123"))
            .roles(Set.of(Role.USER))
            .balance(20000.0)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        log.info("Users created: {}, {}, {}, {}",
            user1.getEmail(), user2.getEmail(), user3.getEmail(), user4.getEmail());

        // Seed Transactions
        if (transactionRepository.count() == 0) {
          Transaction tx1 = Transaction.builder()
              .intentId("pi_" + System.currentTimeMillis())
              .clientSecret("secret_" + System.currentTimeMillis())
              .userId(user1.getId())
              .senderUpiId(user1.getUpiId())
              .receiverUpiId(user2.getUpiId())
              .amount(500.0)
              .currency("INR")
              .status(TransactionStatus.SUCCEEDED)
              .description("Payment for dinner")
              .utr("UTR" + System.currentTimeMillis())
              .createdAt(LocalDateTime.now().minusDays(5))
              .updatedAt(LocalDateTime.now().minusDays(5))
              .build();

          Transaction tx2 = Transaction.builder()
              .intentId("pi_" + (System.currentTimeMillis() + 1))
              .clientSecret("secret_" + (System.currentTimeMillis() + 1))
              .userId(user2.getId())
              .senderUpiId(user2.getUpiId())
              .receiverUpiId(user3.getUpiId())
              .amount(1200.0)
              .currency("INR")
              .status(TransactionStatus.SUCCEEDED)
              .description("Rent payment")
              .utr("UTR" + (System.currentTimeMillis() + 1))
              .createdAt(LocalDateTime.now().minusDays(3))
              .updatedAt(LocalDateTime.now().minusDays(3))
              .build();

          Transaction tx3 = Transaction.builder()
              .intentId("pi_" + (System.currentTimeMillis() + 2))
              .clientSecret("secret_" + (System.currentTimeMillis() + 2))
              .userId(user3.getId())
              .senderUpiId(user3.getUpiId())
              .receiverUpiId(user4.getUpiId())
              .amount(300.0)
              .currency("INR")
              .status(TransactionStatus.FAILED)
              .description("Movie tickets")
              .failureCode("INSUFFICIENT_FUNDS")
              .failureMessage("Insufficient balance in account")
              .createdAt(LocalDateTime.now().minusDays(2))
              .updatedAt(LocalDateTime.now().minusDays(2))
              .build();

          Transaction tx4 = Transaction.builder()
              .intentId("pi_" + (System.currentTimeMillis() + 3))
              .clientSecret("secret_" + (System.currentTimeMillis() + 3))
              .userId(user4.getId())
              .senderUpiId(user4.getUpiId())
              .receiverUpiId(user1.getUpiId())
              .amount(2000.0)
              .currency("INR")
              .status(TransactionStatus.SUCCEEDED)
              .description("Refund for cancelled order")
              .utr("UTR" + (System.currentTimeMillis() + 3))
              .createdAt(LocalDateTime.now().minusDays(1))
              .updatedAt(LocalDateTime.now().minusDays(1))
              .build();

          Transaction tx5 = Transaction.builder()
              .intentId("pi_" + (System.currentTimeMillis() + 4))
              .clientSecret("secret_" + (System.currentTimeMillis() + 4))
              .userId(user1.getId())
              .senderUpiId(user1.getUpiId())
              .receiverUpiId(user3.getUpiId())
              .amount(750.0)
              .currency("INR")
              .status(TransactionStatus.PROCESSING)
              .description("Utility bill payment")
              .createdAt(LocalDateTime.now().minusHours(2))
              .updatedAt(LocalDateTime.now().minusHours(2))
              .build();

          transactionRepository.save(tx1);
          transactionRepository.save(tx2);
          transactionRepository.save(tx3);
          transactionRepository.save(tx4);
          transactionRepository.save(tx5);

          log.info("Created {} sample transactions", 5);
        }
      }

      log.info("Database seeding completed successfully!");
    };
  }
}
