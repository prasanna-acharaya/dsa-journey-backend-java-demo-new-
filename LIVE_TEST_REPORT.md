# Live API Test Report - 2025-12-22

This report documents the actual cURL commands and responses captured from a live running instance of the application.

---

## 1. Authentication (Login)
**Goal**: Obtain a JWT token for subsequent requests.

- **Request**:
  ```bash
  curl.exe -X POST -H "Content-Type: application/json" -d @temp_login.json http://localhost:8080/api/v1/auth/login
  ```
- **Response (200 OK)**:
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJEU0FfQUNUSVZFXzAwMSIsImF1dGhvcml0aWVzIjoiUk9MRV9EU0EiLCJpYXQiOjE3NjYzODU0NTAsImV4cCI6MTc2NjM4OTA1MCwiaXNzIjoiZHNhLWxvYW4tbWFuYWdlbWVudCJ9.bb8wqrJBRnTpDnZLbV4tJVrQnp3bww7u8N2C7wTO8WaT8qw5Fl6NrC_GZ5TrpgSQ",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "username": "DSA_ACTIVE_001",
    "dsaUniqueCode": "DSA_ACTIVE_001",
    "fullName": "Safe Loans Pvt Ltd",
    "role": "DSA"
  }
  ```

---

## 2. Dashboard Metrics (DSA)
**Goal**: Verify dashboard stats retrieval for the authenticated DSA.

- **Request**:
  ```bash
  curl.exe -H "Authorization: Bearer <token>" http://localhost:8080/api/v1/dashboard
  ```
- **Response (200 OK)**:
  ```json
  {
    "totalLeads": 9,
    "appliedLeads": 2,
    "underProcessLeads": 1,
    "sanctionedLeads": 1,
    "disbursedLeads": 2,
    "rejectedLeads": 2,
    "leadsByProductType": {
      "LOAN_AGAINST_PROPERTY": 2,
      "VEHICLE_LOAN": 3,
      "EDUCATION_LOAN": 3,
      "HOME_LOAN": 1
    },
    "conversionRate": "22.2%"
  }
  ```

---

## 3. Lead Listing (Paginated)
**Goal**: Verify retrieval of the most recent leads.

- **Request**:
  ```bash
  curl.exe -H "Authorization: Bearer <token>" http://localhost:8080/api/v1/leads?size=2
  ```
- **Response (200 OK)**:
  ```json
  {
    "content": [
      {
        "leadId": "7e751e71-3399-48c9-a8f0-da340b670c59",
        "applicationReferenceNumber": "BOM2576599",
        "productType": "EDUCATION_LOAN",
        "status": "APPLIED",
        "customerName": "Mr Test Test",
        "amountRequested": 10.00
      },
      {
        "leadId": "a1eebc99-9c0b-4ef8-bb6d-6bb9bd380101",
        "applicationReferenceNumber": "LD-APP-001",
        "productType": "HOME_LOAN",
        "status": "APPLIED",
        "customerName": "John Doe",
        "amountRequested": 5000000.00
      }
    ],
    "totalElements": 9,
    "totalPages": 5,
    "size": 2,
    "number": 0
  }
  ```

---

## 4. Billing Summary
**Goal**: Check recorded earnings for the DSA.

- **Request**:
  ```bash
  curl.exe -H "Authorization: Bearer <token>" http://localhost:8080/api/v1/billing/summary
  ```
- **Response (200 OK)**:
  ```json
  {
    "totalEarned": 30000.00,
    "pendingAmount": 30000.00,
    "paidAmount": 0,
    "totalInvoices": 1
  }
  ```

---

## 5. Validation Error (400 Bad Request)
**Goal**: Verify that sending an empty request results in a structured validation error.

- **Request**:
  ```bash
  curl.exe -X POST -H "Authorization: Bearer <token>" -d "{}" http://localhost:8080/api/v1/leads
  ```
- **Response (400 Bad Request)**:
  ```json
  {
    "status": 400,
    "error": "Validation Failed",
    "message": "One or more fields have validation errors",
    "timestamp": "2025-12-22T06:38:35.190335900Z",
    "fieldErrors": {
      "basicDetails": "Basic details are required",
      "productType": "Product type is required",
      "occupationDetails": "Occupation details are required"
    }
  }
  ```
