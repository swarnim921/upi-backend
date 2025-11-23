# Frontend-Backend Integration Guide

## 🔗 Base URL
```
http://localhost:8080
```

## 📡 CORS (Cross-Origin Resource Sharing)
The backend is configured to allow requests from any origin (`*`) for development purposes.
*   **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS
*   **Allowed Headers**: Authorization, Content-Type

## 🔐 Authentication Integration

### Sending Requests
For any endpoint requiring authentication, you **MUST** include the JWT token in the header.

**JavaScript (Fetch) Example:**
```javascript
const token = localStorage.getItem('jwt_token');

fetch('http://localhost:8080/api/users/me', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

**Axios Example:**
```javascript
const token = localStorage.getItem('jwt_token');

axios.get('http://localhost:8080/api/users/me', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
.then(response => console.log(response.data));
```

## 🔄 Integration Workflows

### 1. Login Flow
1.  **Frontend**: Create a form with Email and Password.
2.  **Action**: POST to `/api/auth/login`.
3.  **Success**:
    *   Save `response.token` to `localStorage`.
    *   Save `response.user` details if needed.
    *   Redirect to Dashboard.
4.  **Error**: Display "Invalid credentials".

### 2. Payment Flow
1.  **Step 1 (Intent)**:
    *   User enters Amount and Payee UPI ID.
    *   POST to `/api/payment/intent`.
    *   **Store** `transactionId` from response.
    *   Show "Enter OTP" modal.
2.  **Step 2 (Confirm)**:
    *   User enters OTP.
    *   POST to `/api/payment/confirm` with `transactionId` and `otp`.
    *   **Success**: Show "Payment Successful" animation.
    *   **Error**: Show "Invalid OTP, try again".

### 3. Transaction History
1.  **Frontend**: Dashboard "History" tab.
2.  **Action**: GET `/api/transactions` (or `/api/users/me` if history is included in profile).
3.  **Display**: List of transactions with Date, Amount, and Status (Green for Success, Red for Failed).

## 🐞 Debugging
If you get a **403 Forbidden**:
1.  Check if the token is present in `localStorage`.
2.  Check if the header is exactly `Authorization: Bearer <token>`.
3.  Check if the token has expired (decode it to check `exp`).
