# 🚀 Backend Update: Integration Fixes

Hey Team,

I've just pushed some critical updates to the backend to fix the integration issues with the Frontend.

### 🛠️ What Changed:
1.  **Fixed Google Login**: The backend now correctly redirects to the frontend callback URL (`/oauth/callback`) after login.
2.  **Added Wallet Endpoint**: `GET /api/users/wallet` is now available (was missing before).
3.  **Fixed Payment Flow**: Added `POST /api/payments/send` to support direct payments as expected by the frontend.
4.  **Fixed Transactions**: Added `GET /api/payments/transactions` so the history tab works.

### ⚠️ Action Required:
Please pull the latest changes and restart the backend:

```bash
git pull origin main
mvn spring-boot:run
```

The Frontend should now work seamlessly with the Backend! Let me know if you face any issues.
