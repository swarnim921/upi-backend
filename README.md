# UPI Dashboard Backend

## 📌 Project Overview
The **UPI Dashboard Backend** is a robust Spring Boot application designed to facilitate secure and efficient Unified Payments Interface (UPI) operations. It powers the core banking features including user authentication, real-time payments, transaction history, and administrative oversight.

## 🚀 Features
*   **Secure Authentication**: JWT-based login and registration.
*   **Payment Processing**: Intent creation, OTP verification, and fund transfer.
*   **Wallet Management**: Real-time balance updates.
*   **Refund System**: Automated refund processing for failed transactions.
*   **Admin Dashboard**: Comprehensive view of system transactions.
*   **MongoDB Integration**: Scalable NoSQL database for high-volume data.

## 🛠️ Tech Stack
*   **Java 17**
*   **Spring Boot 3**
*   **MongoDB**
*   **Maven**
*   **JWT (JSON Web Tokens)**
*   **Swagger / OpenAPI**

## 📚 Documentation
We have comprehensive documentation available in the `docs/` folder:

*   **[Backend Architecture](docs/backend/ARCHITECTURE.md)**: System design, folder structure, and DB schema.
*   **[Component Guide](docs/backend/COMPONENTS.md)**: Details on Controllers, Services, and DTOs.
*   **[API Reference](docs/api/API_REFERENCE.md)**: Full list of endpoints with examples.
*   **[Integration Guide](docs/integration/FRONTEND_BACKEND_INTEGRATION.md)**: Guide for frontend developers.
*   **[User Manual](docs/user-manual/USER_MANUAL.md)**: Non-technical guide for end-users.

## 🏃 How to Run

### Prerequisites
*   Java 17+
*   Maven
*   MongoDB (Running locally on port 27017)

### Steps
1.  **Clone the repository**:
    ```bash
    git clone <repo-url>
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

## 📖 API Documentation (Swagger)
Once the application is running, you can access the interactive Swagger UI at:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

## 🤝 Contributing
1.  Fork the repository.
2.  Create a feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.
