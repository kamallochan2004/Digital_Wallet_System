# Digital Wallet System

A comprehensive backend implementation for digital wallet operations built with Spring Boot.

## Features

- **User Management** - Registration and authentication
- **Wallet Operations** - Fund accounts and check balances  
- **Payment System** - Peer-to-peer money transfers
- **Product Marketplace** - Buy and sell digital products
- **Transaction History** - Complete audit trail

## Technology Stack

- **Framework**: Spring Boot 3.5.3
- **Language**: Java 21
- **Database**: PostgreSQL (Neon)
- **Security**: Spring Security with BCrypt
- **Architecture**: RESTful API

## API Base URL

```
https://digital-wallet-backend-service.onrender.com/api/v1
```

*The application is deployed on Render, a public cloud service, for easy access and testing.*

## API Documentation

For detailed API documentation with all endpoints, request/response formats, and examples, see [API Documentation](api.md).

## Quick Test

```bash
# Register a user
curl -X POST https://digital-wallet-backend-service.onrender.com/api/v1/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}'

# Fund account (use Basic Auth: demo:demo123)
curl -X POST https://digital-wallet-backend-service.onrender.com/api/v1/fund \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic ZGVtbzpkZW1vMTIz" \
  -d '{"amt":100.00}'
```

> **Note**: `ZGVtbzpkZW1vMTIz` is Base64 encoding of `demo:demo123`
