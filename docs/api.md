# API Documentation

Complete API reference for the Digital Wallet System with OpenAPI specification format.

## Base URL
```
https://digital-wallet-backend-service.onrender.com/api/v1
```

## Authentication
Most endpoints require HTTP Basic Authentication. Use the credentials obtained during registration.

**Header Format:**
```
Authorization: Basic <base64(username:password)>
```

---

## Endpoints

### 1. Register User
Create a new user account.

**`POST /register`**

- **Authentication:** Not required
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Example:**
```bash
curl -X POST https://digital-wallet-backend-service.onrender.com/api/v1/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"alice123"}'
```

**Response (201 Created):**
```json
{
  "message": "User registered successfully"
}
```

**Error Responses:**
- **400 Bad Request** - Invalid input data
  ```json
  {
    "error": "Username must be between 3-50 characters"
  }
  ```
- **409 Conflict** - Username already exists
  ```json
  {
    "error": "Username already exists"
  }
  ```

---

### 2. Fund Account
Add money to the user's wallet.

**`POST /fund`**

- **Authentication:** Required
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "amt": "number"
}
```

**Example:**
```bash
curl -X POST https://digital-wallet-backend-service.onrender.com/api/v1/fund \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWxpY2U6YWxpY2UxMjM=" \
  -d '{"amt":100.50}'
```

**Response (200 OK):**
```json
{
  "balance": 100.50
}
```

**Error Responses:**
- **401 Unauthorized** - Invalid credentials
- **400 Bad Request** - Invalid amount
  ```json
  {
    "error": "Amount must be a positive number"
  }
  ```

---

### 3. Pay Another User
Transfer money to another user's account.

**`POST /pay`**

- **Authentication:** Required
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "to": "string",
  "amt": "number"
}
```

**Example:**
```bash
curl -X POST https://digital-wallet-backend-service.onrender.com/api/v1/pay \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWxpY2U6YWxpY2UxMjM=" \
  -d '{"to":"bob","amt":25.00}'
```

**Response (200 OK):**
```json
{
  "balance": 75.50
}
```

**Error Responses:**
- **401 Unauthorized** - Invalid credentials
- **400 Bad Request** - Insufficient funds
  ```json
  {
    "error": "Insufficient balance"
  }
  ```
- **404 Not Found** - Recipient user not found
  ```json
  {
    "error": "User not found"
  }
  ```

---

### 4. Check Balance
View current account balance with optional currency conversion.

**`GET /bal`**

- **Authentication:** Required
- **Query Parameters:** 
  - `currency` (optional): Currency code for conversion

**Examples:**
```bash
# Check balance in default currency
curl -X GET https://digital-wallet-backend-service.onrender.com/api/v1/bal \
  -H "Authorization: Basic YWxpY2U6YWxpY2UxMjM="

# Check balance with currency conversion
curl -X GET "https://digital-wallet-backend-service.onrender.com/api/v1/bal?currency=EUR" \
  -H "Authorization: Basic YWxpY2U6YWxpY2UxMjM="
```

**Response (200 OK):**
```json
{
  "balance": 75.5000,
  "currency": "INR"
}
```

**With currency conversion:**
```json
{
  "balance": 0.76,
  "currency": "EUR"
}
```

**Error Responses:**
- **401 Unauthorized** - Invalid credentials
- **400 Bad Request** - Invalid currency code
  ```json
  {
    "error": "Invalid currency code"
  }
  ```

---

### 5. View Transaction History
Get a list of all user transactions.

**`GET /stmt`**

- **Authentication:** Required

**Example:**
```bash
curl -X GET https://digital-wallet-backend-service.onrender.com/api/v1/stmt \
  -H "Authorization: Basic YWxpY2U6YWxpY2UxMjM="
```

**Response (200 OK):**
```json
[
  {
    "kind": "debit",
    "amt": 25.0000,
    "updated_bal": 75.5000,
    "timestamp": "2025-06-22T04:31:28.874046"
  },
  {
    "kind": "credit",
    "amt": 100.5000,
    "updated_bal": 100.5000,
    "timestamp": "2025-06-22T04:30:09.277573"
  }
]
```

**Error Responses:**
- **401 Unauthorized** - Invalid credentials

---

### 6. Add Product
Add a new product to the marketplace.

**`POST /product`**

- **Authentication:** Required
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "name": "string",
  "price": "number",
  "description": "string"
}
```

**Example:**
```bash
curl -X POST https://digital-wallet-backend-service.onrender.com/api/v1/product \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWxpY2U6YWxpY2UxMjM=" \
  -d '{
    "name": "Digital Course",
    "price": 49.99,
    "description": "Comprehensive programming course"
  }'
```

**Response (201 Created):**
```json
{
  "id": "123",
  "message": "Product added"
}
```

**Error Responses:**
- **401 Unauthorized** - Invalid credentials
- **400 Bad Request** - Invalid product data
  ```json
  {
    "error": "Product name is required"
  }
  ```

---

### 7. List All Products
Retrieve all available products in the marketplace.

**`GET /product`**

- **Authentication:** Not required

**Example:**
```bash
curl -X GET https://digital-wallet-backend-service.onrender.com/api/v1/product
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Digital Course",
    "price": 49.99,
    "description": "Comprehensive programming course"
  }
]
```
---

### 8. Buy Product
Purchase a product using wallet balance.

**`POST /buy`**

- **Authentication:** Required
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "product_id": "number"
}
```

**Example:**
```bash
curl -X POST https://digital-wallet-backend-service.onrender.com/api/v1/buy \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWxpY2U6YWxpY2UxMjM=" \
  -d '{"product_id":1}'
```

**Response (200 OK):**
```json
{
  "message": "Product purchased",
  "balance": 25.51
}
```

**Error Responses:**
- **401 Unauthorized** - Invalid credentials
- **400 Bad Request** - Insufficient funds
  ```json
  {
    "error": "Insufficient balance to purchase this product."
  }
  ```
- **404 Not Found** - Product not found
  ```json
  {
    "error": "Product ID not found"
  }
  ```

---

## Error Responses

All endpoints return standard HTTP status codes:

- **400 Bad Request** - Invalid request data
- **401 Unauthorized** - Authentication required or invalid credentials
- **404 Not Found** - Resource not found (user, product)
- **409 Conflict** - Resource already exists (username taken)
- **500 Internal Server Error** - Server error

**Error Response Format:**
```json
{
  "error": "Detailed error description"
}
```

**Example Error Response:**
```json
{
  "error": "Insufficient balance to purchase this product."
}
```

---

## Data Types

| Field | Type | Constraints |
|-------|------|-------------|
| username | string | 3-50 characters, alphanumeric |
| password | string | Minimum 6 characters |
| amt | number | Positive decimal, up to 2 decimal places |
| product_id | number | Valid product ID |
| name | string | 1-255 characters |
| description | string | Up to 1000 characters |

---

## Authentication Helper

To generate Base64 encoding for Basic Auth:

```bash
echo -n "username:password" | base64
```

**Common encodings:**
- `alice:alice123` → `YWxpY2U6YWxpY2UxMjM=`
- `bob:bob123` → `Ym9iOmJvYjEyMw==`
- `demo:demo123` → `ZGVtbzpkZW1vMTIz`
