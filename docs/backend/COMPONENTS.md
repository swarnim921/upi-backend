# Backend Components

## 📦 DTOs (Data Transfer Objects)
DTOs are used to transfer data between the client and the server. They ensure that internal database models are not directly exposed.

*   **`AuthRequest`**: Login credentials (`email`, `password`).
*   **`RegisterRequest`**: Registration details (`username`, `email`, `password`).
*   **`PaymentIntentRequest`**: Details to initiate payment (`amount`, `payeeUpiId`, `description`).
*   **`PaymentConfirmationRequest`**: OTP for payment (`transactionId`, `otp`).
*   **`RefundRequest`**: Refund details (`transactionId`, `reason`).
*   **`UserDto`**: Public user profile info.

## 🗃️ Models (Entities)
These classes map directly to MongoDB collections.

*   **`User`**: Represents a registered user.
*   **`Transaction`**: Records details of every financial operation.
*   **`Wallet`**: Stores user balance and currency.

## ⚙️ Services
Business logic resides here.

*   **`AuthService`**: Handles registration and login logic.
*   **`UserService`**: Manages user profiles and wallet retrieval.
*   **`PaymentService`**: Orchestrates the complex payment flow (Intent -> OTP -> Transfer).
*   **`TransactionService`**: Handles transaction history and refunds.
*   **`CustomUserDetailsService`**: Loads user data for Spring Security.

## 🎮 Controllers
REST endpoints that handle HTTP requests.

*   **`AuthController`**: `/api/auth` (Register, Login).
*   **`UserController`**: `/api/users` (Profile, Search).
*   **`PaymentController`**: `/api/payment` (Intent, Confirm, Refund).
*   **`TransactionController`**: `/api/transactions` (History).
*   **`AdminController`**: `/api/admin` (System-wide views).
*   **`HealthController`**: `/api/health` (System status).
