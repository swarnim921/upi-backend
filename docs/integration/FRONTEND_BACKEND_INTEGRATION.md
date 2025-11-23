# 🔌 Frontend-Backend Integration Guide
> **Version:** 1.1 | **Last Updated:** 2025-11-23

## 1. Login & JWT Storage

### Login API Call
**Endpoint:** `POST /api/auth/login`

**Example (Fetch):**
```javascript
async function login(email, password) {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  
  const data = await response.json();
  
  if (response.ok) {
    // Store token in localStorage
    localStorage.setItem('jwt_token', data.token);
    // Store user info if needed
    localStorage.setItem('user_info', JSON.stringify(data));
  } else {
    console.error('Login failed:', data.error);
  }
}
```

**Example (Axios):**
```javascript
async function login(email, password) {
  try {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      email, password
To access protected endpoints (like Profile, Payment), you **MUST** include the JWT token in the `Authorization` header.

**Header Format:**
```javascript
headers: {
  'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`,
  'Content-Type': 'application/json'
}
```

**Example: Get Profile**
```javascript
async function getProfile() {
  const token = localStorage.getItem('jwt_token');
  
  const response = await fetch('http://localhost:8080/api/users/me', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const profile = await response.json();
  console.log(profile);
}
```

---

## 3. Payment Integration Flow

### Step A: Create Payment Intent
1.  User enters Amount and Payee UPI ID.
2.  Frontend calls `POST /api/payment/intent`.
3.  Backend sends OTP to user's mobile (simulated).
4.  Frontend receives `transactionId`.

```javascript
const intentResponse = await axios.post('http://localhost:8080/api/payment/intent', {
  amount: 500,
  payeeUpiId: 'merchant@upi'
}, { headers: authHeader });

const txnId = intentResponse.data.transactionId;
// Show OTP Input Modal
```

### Step B: Confirm Payment
1.  User enters OTP.
2.  Frontend calls `POST /api/payment/confirm`.

```javascript
const confirmResponse = await axios.post('http://localhost:8080/api/payment/confirm', {
  transactionId: txnId,
  otp: '123456' // User input
}, { headers: authHeader });

if (confirmResponse.data.status === 'SUCCESS') {
  // Show Success Animation
}
```

### Step C: Refund Flow
1.  User selects a transaction from History.
2.  Frontend calls `POST /api/payment/refund`.

```javascript
await axios.post('http://localhost:8080/api/payment/refund', {
  transactionId: 'txn_98765',
  reason: 'Accidental transfer'
}, { headers: authHeader });
```

---

## 4. Common Errors & Solutions

| Error | Cause | Solution |
| :--- | :--- | :--- |
| `401 Unauthorized` | Token missing or expired | Redirect user to Login page. |
| `403 Forbidden` | User lacks permission | Check user roles. |
| `400 Bad Request` | Invalid input (e.g., email) | Show validation error message to user. |
| `500 Internal Error` | Server issue | Show "Something went wrong, try again later". |
