# API Specifications - Shortify

All requests from the client flow through the **API Gateway** on port `8080`. The gateway forwards requests to their target services.

---

## 🔒 Authentication Service (`/api/auth/*`)

Performs user registration, login, and profile operations. Protected routes require a Bearer token in the `Authorization` header.

### 1. User Registration
* **Endpoint**: `POST /api/auth/register`
* **Access**: Public
* **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "SecurePassword123"
  }
  ```
* **Response (`201 Created`)**:
  ```json
  {
    "id": "c30f40d1-0f73-4c91-9e79-bd37a4c7e2cc",
    "email": "user@example.com",
    "created_at": "2026-06-18T01:15:30Z"
  }
  ```

### 2. User Login
* **Endpoint**: `POST /api/auth/login`
* **Access**: Public
* **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "SecurePassword123"
  }
  ```
* **Response (`200 OK`)**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "user@example.com",
    "expires_in": 86400
  }
  ```

---

## 🔗 URL Service (`/api/urls/*`)

Manages URL creation, listing, deletion, and link redirection.

### 1. Shorten a URL
* **Endpoint**: `POST /api/urls/shorten`
* **Access**: Public / Authenticated (Include Bearer token to save link to user account)
* **Request Body**:
  ```json
  {
    "original_url": "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/302",
    "custom_alias": "http-302",
    "expires_at": "2026-12-31T23:59:59Z"
  }
  ```
* **Response (`201 Created`)**:
  ```json
  {
    "short_url": "http://localhost:8080/s/http-302",
    "short_code": "http-302",
    "original_url": "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/302",
    "created_at": "2026-06-18T01:17:40Z",
    "expires_at": "2026-12-31T23:59:59Z"
  }
  ```

### 2. Resolve short code (Redirection)
* **Endpoint**: `GET /s/{short_code}`
* **Access**: Public
* **Headers**: Sends User-Agent, IP, and Referrer details.
* **Response (`302 Found`)**:
  * Redirects user immediately to the original URL via HTTP Header: `Location: https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/302`

### 3. List User URLs
* **Endpoint**: `GET /api/urls/my-urls`
* **Access**: Authenticated (Requires Bearer token)
* **Response (`200 OK`)**:
  ```json
  [
    {
      "short_code": "http-302",
      "original_url": "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/302",
      "created_at": "2026-06-18T01:17:40Z",
      "clicks_count": 42
    }
  ]
  ```

### 4. Delete Link
* **Endpoint**: `DELETE /api/urls/{short_code}`
* **Access**: Authenticated (Requires Bearer token)
* **Response (`204 No Content`)**

---

## 📊 Analytics Service (`/api/analytics/*`)

Consumes click metrics and provides aggregation read-APIs for user dashboards.

### 1. Get Link Statistics
* **Endpoint**: `GET /api/analytics/{short_code}`
* **Access**: Authenticated (Requires Bearer token, must own the link)
* **Response (`200 OK`)**:
  ```json
  {
    "short_code": "http-302",
    "total_clicks": 42,
    "browsers": {
      "Chrome": 30,
      "Firefox": 8,
      "Safari": 4
    },
    "countries": {
      "IN": 25,
      "US": 15,
      "GB": 2
    },
    "referrers": {
      "direct": 20,
      "https://t.co/": 15,
      "https://github.com/": 7
    }
  }
  ```
