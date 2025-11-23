# 📚 API Reference
> **Version:** 1.2 | **Last Updated:** 2025-11-23

## 🌐 Base URL
**Base URL:** `http://localhost:8080/api`

## 🔗 Swagger Documentation
*   **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
*   **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## 🛠️ Headers
All authenticated endpoints require the following headers:
```http
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>
```

---

## 👥 Authentication & User Management

### 1. Register User
**Endpoint:** `POST /auth/register`
**Access:** Public

**Request Body:**
```json
{
  "username": "John Doe",  // Required, Not Empty
  "email": "john@example.com", // Required, Valid Email
  "password": "password123" // Required, Min 6 chars
}
```

**Response (200 OK):**
```json
{
  "id": "653a1b2c...",
  "username": "John Doe",
  "email": "john@example.com",
  "upiId": "john@upi",
  "walletId": "w_12345",
  "roles": ["ROLE_USER"]
}
```

### 2. Login
**Endpoint:** `POST /auth/login`
**Access:** Public

**Request Body:**
```json
{
  "email": "john@example.com", // Required
  "password": "password123" // Required
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": "653a1b2c...",
  "username": "John Doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### 3. Google Login
**Endpoint:** `GET /api/auth/google`
**Access:** Public

**Response (200 OK):**
```json
{
  "authorizationUrl": "https://accounts.google.com/o/oauth2/v2/auth?...",
  "message": "Redirect to /oauth2/authorization/google to initiate Google OAuth2 login"
}
```

**Flow:**
1. Frontend calls `/api/auth/google` to get the redirect URL.
2. Frontend redirects user to that URL.
3. User logs in with Google.
4. Google redirects back to backend (handled automatically).
5. Backend redirects to Frontend Callback URL (`http://localhost:5173/oauth/callback`) with query params:
   `?token=...&id=...&username=...&email=...&roles=...`

### 4. Get My Profile
**Endpoint:** `GET /users/me`
**Access:** USER

**Response (200 OK):**
```json
{
  "id": "653a1b2c...",
  "username": "John Doe",
  "email": "john@example.com",
  "upiId": "john@upi",
  "balance": 1000.00
}
```

### 5. Get My Wallet
**Endpoint:** `GET /users/wallet`
**Access:** USER

**Response (200 OK):**
```json
{
  "walletId": "w_653a1b2c...",
  "balance": 1000.00,
  "currency": "INR"
}
  "payeeUpiId": "merchant@upi", // Required
  "description": "Grocery shopping" // Optional
}
```

**Response (200 OK):**
```json
{
  "transactionId": "txn_98765",
  "status": "PENDING",
  "message": "OTP sent to registered mobile number"
}
```

### 2. Confirm Payment
**Endpoint:** `POST /payment/confirm`
**Access:** USER

**Request Body:**
```json
{
  "transactionId": "txn_98765", // Required
  "otp": "123456" // Required
}
```

**Response (200 OK):**
```json
{
  "transactionId": "txn_98765",
  "status": "SUCCESS",
  "message": "Payment successful"
}
```

### 3. Refund Transaction
**Endpoint:** `POST /payment/refund`
**Access:** USER

**Request Body:**
```json
{
  "transactionId": "txn_98765", // Required
  "reason": "Wrong amount" // Required
}
```

**Response (200 OK):**
```json
{
  "refundId": "ref_54321",
  "originalTransactionId": "txn_98765",
  "status": "PROCESSED",
  "message": "Refund initiated successfully"
}
```

---

## 👑 Admin

### 1. List All Transactions
**Endpoint:** `GET /admin/transactions`
**Access:** ADMIN

**Response (200 OK):**
```json
[
  {
    "id": "txn_98765",
    "amount": 500.00,
    "sender": "john@upi",
    "receiver": "merchant@upi",
    "status": "SUCCESS",
    "timestamp": "2023-10-27T10:30:00"
  }
]
```

---

## ⚠️ Error Responses

### Validation Error
**Status:** `400 Bad Request`
```json
{
  "email": "Invalid email format",
  "password": "Password must be at least 6 characters long"
}
```

### Generic Error
**Status:** `400/401/403/404/500`
```json
{
  "error": "User not found"
}
```
