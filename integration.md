# Frontend-Backend Integration Guide

## 🎯 Purpose
This document serves as a guide for frontend developers to integrate with the UPI Dashboard Backend. It details authentication mechanisms, API endpoints, request/response structures, and error handling.

## 🌐 Base API URL
All API requests should be made to:
```
http://localhost:8080
```

## 🔐 Authentication
The backend uses **JWT (JSON Web Token)** for security.

### 1. Obtain Token
- Call the **Login API** (`/api/auth/login`) with valid credentials.
- The response will contain a `token`.

### 2. Use Token
- Store the token securely (e.g., `localStorage` or `HttpOnly Cookie`).
- Include the token in the **Authorization header** for all protected routes:
  ```http
  Authorization: Bearer <your_jwt_token>
  ```

## 📡 API Request & Response Examples

### 1. Login
**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": "651234567890abcdef",
  "username": "user123",
  "email": "user@example.com"
}
```

### 2. Fetch Profile
**Endpoint:** `GET /api/users/me`
**Auth:** Required

**Response:**
```json
{
  "id": "651234567890abcdef",
  "username": "user123",
  "email": "user@example.com",
  "upiId": "user123@upi",
  "balance": 5000.00,
  "walletId": "w_123456789"
}
```

### 3. Create Payment Intent
**Endpoint:** `POST /api/payment/intent`
**Auth:** Required

**Request:**
```json
{
  "amount": 500.00,
  "payeeUpiId": "merchant@upi",
  "description": "Dinner payment"
}
```

**Response:**
```json
{
  "transactionId": "txn_987654321",
  "status": "PENDING",
  "message": "OTP sent to registered mobile number"
}
```

### 4. Confirm OTP
**Endpoint:** `POST /api/payment/confirm`
**Auth:** Required

**Request:**
```json
{
  "transactionId": "txn_987654321",
  "otp": "123456"
}
```

**Response:**
```json
{
  "transactionId": "txn_987654321",
  "status": "SUCCESS",
  "message": "Payment successful"
}
```

### 5. Refund
**Endpoint:** `POST /api/payment/refund`
**Auth:** Required

**Request:**
```json
{
  "transactionId": "txn_987654321",
  "reason": "Accidental double payment"
}
```

**Response:**
```json
{
  "refundId": "ref_567890123",
  "originalTransactionId": "txn_987654321",
  "status": "PROCESSED",
  "message": "Refund initiated successfully"
}
```

### 6. Admin List Transactions
**Endpoint:** `GET /api/admin/transactions`
**Auth:** Required (Admin Role)

**Response:**
```json
[
  {
    "id": "txn_987654321",
    "amount": 500.00,
    "sender": "user123@upi",
    "receiver": "merchant@upi",
    "status": "SUCCESS",
    "timestamp": "2023-10-27T10:30:00Z"
  },
  {
    "id": "txn_123456789",
    "amount": 150.00,
    "sender": "alice@upi",
    "receiver": "bob@upi",
    "status": "FAILED",
    "timestamp": "2023-10-27T11:15:00Z"
  }
]
```

## ⚠️ Error Handling

### Global Error Format
All errors return a consistent JSON structure.

**Example (Resource Not Found):**
```json
{
  "error": "User not found"
}
```

**Example (Unauthorized):**
```json
{
  "error": "Invalid or expired token"
}
```

### Validation Errors
If input validation fails (e.g., invalid email), the response will be a map of field names to error messages.

**Example:**
```json
{
  "email": "Invalid email format",
  "password": "Password must be at least 6 characters long",
  "amount": "Amount must be greater than zero"
}
```
