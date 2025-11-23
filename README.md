# UPI Dashboard Backend

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

## 🔗 Swagger API Documentation
Once the application is running, you can access the interactive API docs:
*   **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
*   **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## 🔐 Authentication Flow
1.  **Register**: POST `/api/auth/register` to create an account.
2.  **Login**: POST `/api/auth/login` to get a **JWT Token**.
3.  **Access**: Include the token in the header for all subsequent requests:
    `Authorization: Bearer <your_token>`

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
