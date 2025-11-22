# API Reference

## 🔐 Authentication
All protected endpoints require a valid JWT token in the `Authorization` header.
```http
Authorization: Bearer <your_token_here>
```

## 👥 User Management

### 1. Register User
**Endpoint:** `POST /api/auth/register`
**Access:** Public

**Request:**
```json
{
  "username": "John Doe",
  "email": "john@example.com",
  "password": "password123"
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
**Endpoint:** `POST /api/auth/login`
**Access:** Public

**Request:**
```json
{
  "email": "john@example.com",
  "password": "password123"
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

### 3. Get My Profile
**Endpoint:** `GET /api/users/me`
**Access:** User

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

## 💸 Payments

### 1. Create Payment Intent
Initiates a payment request.
**Endpoint:** `POST /api/payment/intent`
**Access:** User

**Request:**
```json
{
  "amount": 500.00,
  "payeeUpiId": "merchant@upi",
  "description": "Grocery shopping"
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
Completes the transaction using OTP.
**Endpoint:** `POST /api/payment/confirm`
**Access:** User

**Request:**
```json
{
  "transactionId": "txn_98765",
  "otp": "123456"
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
**Endpoint:** `POST /api/payment/refund`
**Access:** User

**Request:**
```json
{
  "transactionId": "txn_98765",
  "reason": "Wrong amount"
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

## 👑 Admin

### 1. List All Transactions
**Endpoint:** `GET /api/admin/transactions`
**Access:** Admin Only

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

## ⚠️ Error Codes

| Status | Error | Description |
| :--- | :--- | :--- |
| `400` | `Bad Request` | Invalid input (e.g., missing fields, invalid email). |
| `401` | `Unauthorized` | Missing or invalid JWT token. |
| `403` | `Forbidden` | User does not have permission (e.g., non-admin accessing admin API). |
| `404` | `Not Found` | Resource (User, Transaction) not found. |
| `500` | `Internal Server Error` | Unexpected server failure. |
