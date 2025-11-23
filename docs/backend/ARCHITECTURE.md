# Backend Architecture

## 🏗️ Architecture Overview
The UPI Dashboard Backend follows a standard **Layered Architecture** typical of Spring Boot applications. This ensures separation of concerns, maintainability, and scalability.

### Layers
1.  **Controller Layer (`controller`)**: Handles incoming HTTP requests, validates input, and returns responses.
2.  **Service Layer (`service`)**: Contains business logic, processes data, and orchestrates operations.
3.  **Repository Layer (`repository`)**: Manages data persistence and retrieval using MongoDB.
4.  **Model/DTO Layer (`model`, `dto`)**: Defines data structures for database entities and API data transfer.
5.  **Security Layer (`security`, `config`)**: Handles authentication (JWT) and authorization.

## 📂 Folder Structure
```
src/main/java/com/upidashboard/upi_backend/
├── config/          # Configuration classes (Swagger, Security)
├── controller/      # REST Controllers (API Endpoints)
├── dto/             # Data Transfer Objects (Request/Response bodies)
├── model/           # MongoDB Documents (Database Entities)
├── repository/      # MongoDB Repositories (Data Access)
├── service/         # Business Logic Services
├── security/        # JWT Filter, User Details Service
├── exception/       # Global Exception Handler
└── UpiBackendApplication.java  # Main Entry Point
```

## 🔐 JWT Authentication Flow
1.  **Login**: User sends credentials (`email`, `password`) to `/api/auth/login`.
2.  **Verification**: `AuthenticationManager` verifies credentials against the database.
3.  **Token Generation**: If valid, a **JWT (JSON Web Token)** is generated using `JwtUtils`.
4.  **Response**: The token is returned to the client.
5.  **Protected Requests**:
    *   Client sends the token in the `Authorization` header: `Bearer <token>`.
    *   `JwtAuthenticationFilter` intercepts the request.
    *   Validates the token signature and expiration.
    *   Sets the `Authentication` object in the `SecurityContext`.
    *   Request proceeds to the Controller.

## 🗄️ Database Schema (MongoDB)

### `User` Collection
| Field | Type | Description |
| :--- | :--- | :--- |
| `_id` | ObjectId | Unique Identifier |
| `username` | String | User's display name |
| `email` | String | Unique email address |
| `password` | String | BCrypt hashed password |
| `upiId` | String | Unique UPI ID (e.g., `user@upi`) |
| `walletId` | String | Linked Wallet ID |
| `roles` | Set<String> | User roles (ROLE_USER, ROLE_ADMIN) |

### `Transaction` Collection
| Field | Type | Description |
| :--- | :--- | :--- |
| `_id` | ObjectId | Unique Transaction ID |
| `amount` | BigDecimal | Transaction amount |
| `senderId` | String | UPI ID of sender |
| `receiverId` | String | UPI ID of receiver |
| `status` | Enum | SUCCESS, FAILED, PENDING |
| `timestamp` | LocalDateTime | Time of transaction |
| `type` | Enum | PAYMENT, REFUND |

### `Wallet` Collection
| Field | Type | Description |
| :--- | :--- | :--- |
| `_id` | ObjectId | Unique Wallet ID |
| `userId` | String | Owner's User ID |
| `balance` | BigDecimal | Current balance |
| `currency` | String | Currency code (INR) |
