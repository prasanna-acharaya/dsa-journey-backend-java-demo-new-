# DSA Loan Management - Complete API Reference

This document provides a comprehensive list of all API endpoints with cURL examples, response structures, and status codes.

---

## 1. Authentication Manager (`/api/v1/auth`)

### 🔑 User Login
- **Endpoint**: `POST /api/v1/auth/login`
- **Description**: Authenticates a user and returns a JWT token.
- **Status Codes**: `200 OK`, `401 Unauthorized`
- **cURL**:
  ```bash
  curl -X POST http://localhost:8080/api/v1/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"dsa_user","password":"password123"}'
  ```
- **Response**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "dsa_user",
    "roles": ["ROLE_DSA"]
  }
  ```

---

## 2. Lead Management (`/api/v1/leads`)

### ➕ Create Lead
- **Endpoint**: `POST /api/v1/leads`
- **Description**: Creates a new loan lead.
- **Status Codes**: `201 Created`, `400 Bad Request`
- **cURL**:
  ```bash
  curl -X POST http://localhost:8080/api/v1/leads \
    -H "Authorization: Bearer <token>" \
    -H "Content-Type: application/json" \
    -d '{
      "productType": "VEHICLE_LOAN",
      "basicDetails": {
        "firstName": "John", "lastName": "Doe",
        "mobileNumber": "9876543210", "emailAddress": "john@example.com",
        "dateOfBirth": "1990-01-01"
      },
      "loanDetails": { "amountRequested": 500000, "repaymentPeriod": 60 }
    }'
  ```
- **Response**:
  ```json
  {
    "leadId": "550e8400-e29b-41d4-a716-446655440000",
    "applicationReferenceNumber": "BOM20231222001",
    "status": "APPLIED"
  }
  ```

### 🔍 Search Leads
- **Endpoint**: `GET /api/v1/leads`
- **Params**: `status`, `productType`, `searchTerm`, `page`, `size`
- **Status Codes**: `200 OK`
- **Response**: Paginated `LeadSummaryResponse` object.

### 📄 Get Lead by ID
- **Endpoint**: `GET /api/v1/leads/{leadId}`
- **Status Codes**: `200 OK`, `404 Not Found`
- **Response**: Full `LeadResponse` object.

### ✏️ Update Lead
- **Endpoint**: `PUT /api/v1/leads/{leadId}`
- **Status Codes**: `200 OK`, `400 Bad Request`
- **Response**: Updated `LeadResponse` object.

### 🗑️ Delete Lead (Soft Delete)
- **Endpoint**: `DELETE /api/v1/leads/{leadId}`
- **Status Codes**: `204 No Content`

---

## 3. Billing & Commissions (`/api/v1/billing`)

### � Get Billing Summary
- **Endpoint**: `GET /api/v1/billing/summary`
- **Status Codes**: `200 OK`
- **Response**:
  ```json
  {
    "totalEarned": 15000.0, "pendingAmount": 5000.0,
    "paidAmount": 10000.0, "totalInvoices": 5
  }
  ```

### 🧾 List Billings (Paginated)
- **Endpoint**: `GET /api/v1/billing`
- **Status Codes**: `200 OK`
- **Response**: Paginated `BillingResponse`.

### � Get Billing by ID
- **Endpoint**: `GET /api/v1/billing/{billingId}`

---

## 4. Dashboard Analytics (`/api/v1/dashboard`)

### � DSA Dashboard Stats
- **Endpoint**: `GET /api/v1/dashboard`
- **Status Codes**: `200 OK`
- **Response**: `DashboardAnalyticsResponse` (Total leads, status counts).

### � Admin Overall Analytics
- **Endpoint**: `GET /api/v1/dashboard/admin`
- **Permissions**: `ROLE_ADMIN`

---

## 5. DSA Onboarding (`/api/v1/dsa`)

### 🏗️ Create DSA Profile
- **Endpoint**: `POST /api/v1/dsa`
- **Response**: `DsaResponseDto`.

### ✅ Update DSA Status (Approval)
- **Endpoint**: `PUT /api/v1/dsa/{id}/status?status=EMPANELLED`
- **Permissions**: `ROLE_ADMIN`, `ROLE_CHECKER`

---

## 🛑 Error Identification & Validation (Status 400)

When a request fails validation (missing fields, wrong format), the system returns a `400 Bad Request`.

- **Response Format**:
  ```json
  {
    "status": 400,
    "error": "Validation Failed",
    "message": "One or more fields have validation errors",
    "timestamp": "2023-12-22T12:00:00Z",
    "fieldErrors": {
      "basicDetails.mobileNumber": "Mobile number must be 10 digits",
      "loanDetails.amountRequested": "Must be greater than 0"
    }
  }
  ```
