# UPI Dashboard - User Manual

## 👋 Introduction
Welcome to the UPI Dashboard! This system allows you to securely manage your digital money, send payments to friends and merchants, and track all your transactions.

## 🚀 Getting Started

### How to Run the Backend
1.  **Install Java**: Make sure you have Java 17 installed.
2.  **Download**: Get the project files.
3.  **Run**: Open a terminal in the project folder and type:
    ```bash
    mvn spring-boot:run
    ```
4.  **Access**: Open your browser to `http://localhost:8080`.

### Configuration
The system uses a default configuration. If you need to change the database connection, edit the `application.properties` file:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/your_db_name
```

## 📖 Feature Guide

### 1. Registration
*   **Goal**: Create a new account.
*   **Steps**:
    1.  Click "Register".
    2.  Enter your Name, Email, and a secure Password.
    3.  Click "Submit".
    4.  You will receive a unique **UPI ID** (e.g., `yourname@upi`).

### 2. Login
*   **Goal**: Access your account.
*   **Steps**:
    1.  Enter your Email and Password.
    2.  Click "Login".
    3.  You will be taken to your Dashboard.

### 3. Send Money
*   **Goal**: Transfer funds to another user.
*   **Steps**:
    1.  Click the "Send Money" button.
    2.  Enter the **Receiver's UPI ID** (ask them for it!).
    3.  Enter the **Amount** (e.g., 500).
    4.  Add a note (optional, e.g., "Lunch").
    5.  Click "Pay".
    6.  **Security Check**: Enter the OTP sent to your mobile.
    7.  Click "Confirm".
    8.  Wait for the "Success" message.

### 4. View Transactions
*   **Goal**: See where your money went.
*   **Steps**:
    1.  Go to the "History" or "Transactions" tab.
    2.  You will see a list of all payments sent and received.
    3.  Click on a transaction to see more details.

### 5. Request Refund
*   **Goal**: Get money back for a failed or wrong payment.
*   **Steps**:
    1.  Find the transaction in your History.
    2.  Click "Refund".
    3.  Select a reason (e.g., "Sent by mistake").
    4.  Submit the request.
    5.  The system will process it and update your balance.

## ❓ FAQ
*   **I forgot my password**: Contact the system administrator.
*   **Payment Failed**: Check your internet connection and try again. If money was deducted, use the Refund feature.
*   **What is my UPI ID?**: It is displayed at the top of your Dashboard.
