# Digital Wallet System

A comprehensive backend implementation of a digital wallet system built with Spring Boot. This application provides secure user authentication, account management, peer-to-peer payments, and a marketplace for digital products.

## 🚀 Features

- **User Management**
  - Secure user registration and authentication
  - JWT-based authorization
  - Password encryption with BCrypt

- **Wallet Operations**
  - Fund account with various amounts
  - Check balance with multi-currency support
  - Transaction history tracking

- **Payment System**
  - Peer-to-peer money transfers
  - Real-time balance updates
  - Transaction validation and error handling

- **Product Marketplace**
  - Add products to the marketplace
  - Browse available products
  - Purchase products using wallet balance

- **Security Features**
  - Spring Security integration
  - Authentication required for all operations
  - Input validation and sanitization

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.3
- **Language**: Java 21
- **Database**: H2 (In-memory for development)
- **Security**: Spring Security with BCrypt
- **Build Tool**: Maven
- **Architecture**: RESTful API

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## 🔧 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Digitalwallet
   ```

2. **Build the project**
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080/api/v1`
   - H2 Database Console: `http://localhost:8080/h2-console`

## 📚 API Documentation

For detailed API documentation with request/response examples, see [API Documentation](docs/README.md).

### Quick API Overview

| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|----------------|
| `/api/v1/register` | POST | Register new user | No |
| `/api/v1/fund` | POST | Add funds to account | Yes |
| `/api/v1/pay` | POST | Pay another user | Yes |
| `/api/v1/bal` | GET | Check account balance | Yes |
| `/api/v1/stmt` | GET | View transaction history | Yes |
| `/api/v1/product` | POST | Add new product | Yes |
| `/api/v1/product` | GET | List all products | Yes |
| `/api/v1/buy` | POST | Purchase a product | Yes |

## 🔐 Authentication

The application uses Spring Security with HTTP Basic Authentication. After registering, use your username and password for all authenticated endpoints.

## 🏗️ Project Structure

```
src/
├── main/java/com/assignment/Digitalwallet/
│   ├── Config/              # Security and application configuration
│   ├── Controller/          # REST API controllers
│   ├── Dto/                 # Data Transfer Objects
│   ├── Exception/           # Custom exception handlers
│   ├── Model/               # JPA entities
│   ├── Repository/          # Data access layer
│   └── Service/             # Business logic layer
└── resources/
    ├── application.properties
    └── static/              # Static resources
```

## 🧪 Testing

Run the test suite:
```bash
./mvnw test
```

## 📊 Database Schema

The application uses the following main entities:
- **User**: Stores user credentials and account information
- **Transaction**: Records all financial transactions
- **Product**: Marketplace items available for purchase

## 🐳 Docker Support

Build and run with Docker:
```bash
docker build -t digital-wallet .
docker run -p 8080:8080 digital-wallet
```

## 📝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is developed as part of an assignment.

## 🤝 Support

For questions or issues, please create an issue in the repository.