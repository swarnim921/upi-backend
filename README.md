# UPI Dashboard Backend
> **Version:** 1.2 | **Last Updated:** 2025-11-23

## 📌 Project Overview
The **UPI Dashboard Backend** is a secure and scalable Spring Boot application that powers a digital payment system. It handles user authentication, real-time money transfers, wallet management, and transaction history.

## 🚀 Features
*   **Secure Authentication**: JWT-based login and registration.
*   **Real-time Payments**: Send and receive money instantly using UPI IDs.
*   **Wallet System**: Manage user balances and currency.
*   **Refund Processing**: Automated handling of failed or disputed transactions.
*   **Admin Dashboard**: Complete oversight of system transactions.
*   **MongoDB Integration**: High-performance NoSQL database.

## 🛠️ Tech Stack
*   **Language**: Java 17
*   **Framework**: Spring Boot 3
*   **Database**: MongoDB
*   **Security**: Spring Security + JWT
*   **Documentation**: Swagger / OpenAPI
*   **Build Tool**: Maven

## 📂 Documentation
We have detailed documentation available in the `docs/` folder:

*   **[📚 Documentation Index](docs/README_DOCS_INDEX.md)**: Start here!
*   **[🔌 API Reference](docs/api/API_REFERENCE.md)**: Endpoints, request/response examples.
*   **[💻 Integration Guide](docs/integration/FRONTEND_BACKEND_INTEGRATION.md)**: For Frontend Developers.
*   **[📘 User Manual](docs/manual/USER_MANUAL.md)**: For End Users.

## 🏃 How to Run Locally

### Prerequisites
*   Java 17+
*   Maven
*   MongoDB (Running on port 27017)

### Steps
1.  **Clone the repository**:
    ```bash
    git clone https://github.com/swarnim921/upi-backend.git
    cd upi-backend
    ```
2.  **Build the project**:
    ```bash
    mvn clean install -DskipTests
    ```
3.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```
4.  **Access the API**:
    The server will start at `http://localhost:8080`.

## 🧪 Running Tests
To run the automated test suite (Unit & Integration tests):
```bash
mvn test
```
This will verify:
*   Authentication logic (Login, Register)
*   Security configuration (JWT, OAuth2)
*   Service layer logic

## 🔗 Swagger API Documentation
Once the application is running, you can access the interactive API docs:
*   **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
*   **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## 🔐 Authentication Flow
1.  **Register**: POST `/api/auth/register` to create an account.
2.  **Login**: POST `/api/auth/login` to get a **JWT Token**.
3.  **Google Login**: GET `/api/auth/google` to get redirect URL.
4.  **Access**: Include the token in the header for all subsequent requests:
    `Authorization: Bearer <your_token>`

## ☁️ Google Cloud Setup (For SSO)
To enable "Login with Google", you need to set up a project in Google Cloud Console.

1.  Go to [Google Cloud Console](https://console.cloud.google.com/).
2.  Create a new project.
3.  Go to **APIs & Services > Credentials**.
4.  Create **OAuth Client ID**.
    *   **Application Type**: Web Application
    *   **Authorized Redirect URIs**: `http://localhost:8080/login/oauth2/code/google`
5.  Copy the **Client ID** and **Client Secret**.

## ⚙️ Environment Variables
Create a `.env` file (or set system variables) with the following:

```properties
MONGODB_URI=mongodb+srv://...
JWT_SECRET=your_super_secret_key_change_me
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

## 📁 Folder Structure
```
upi-backend/
├── src/main/java/com/upidashboard/upi_backend/
│   ├── config/          # Security & Swagger Config
│   ├── controller/      # API Endpoints
│   ├── dto/             # Request/Response Objects
│   ├── model/           # Database Entities
│   ├── repository/      # MongoDB Access
│   ├── service/         # Business Logic
│   └── security/        # JWT Handling
├── docs/                # Project Documentation
│   ├── api/             # API Reference
│   ├── integration/     # Frontend Guide
│   ├── manual/          # User Manual
│   └── backend/         # Architecture Docs
└── README.md            # Main Project File
```
