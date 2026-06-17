# Shortify - URL Shortener Microservices Application

Shortify is a scalable, production-grade URL Shortener system designed with a microservices backend (Spring Boot, Spring Cloud, Kafka, Redis, MySQL) and a modern frontend (React, Vite, Tailwind CSS, i18next).

---

## 📖 Table of Contents
- [System Architecture](file:///Users/prakhartripathi/Documents/Url-shortner/docs/architecture.md)
- [API Specifications](file:///Users/prakhartripathi/Documents/Url-shortner/docs/api.md)
- [Frontend Documentation](file:///Users/prakhartripathi/Documents/Url-shortner/docs/frontend.md)
- [References & Acknowledgements](file:///Users/prakhartripathi/Documents/Url-shortner/docs/REFERENCES.md)

---

## 🛠️ Technology Stack

### Backend Infrastructure
- **Framework**: Spring Boot 3.x, Spring Cloud (Gateway, Config Server, Eureka)
- **Database**: MySQL (independent instances per service)
- **Caching**: Redis (URL resolution speedups)
- **Event Bus**: Apache Kafka (async tracking/analytics triggers)
- **Monitoring/Tracing**: Micrometer & Zipkin/OpenTelemetry

### Frontend Application
- **Library**: React 19 + Vite
- **Styling**: Tailwind CSS & PostCSS
- **Localization**: i18next (supports English & Hindi)
- **Icons**: Lucide React

---

## 📁 Repository Structure

```text
├── api-gateway/          # Spring Cloud Gateway (Port 8080)
├── eureka-server/        # Eureka Discovery Server
├── config-server/        # Central Configuration Manager
├── auth-service/         # Authentication & User Management (Port 8082)
├── url-service/          # Link generation, resolution & expiry (Port 8081)
├── analytics-service/    # Geolocation, browser & click stats (Port 8083)
├── notification-service/ # Email alerts & expiry notices (Port 8084)
├── frontend/             # React Client Web App
└── docs/                 # Detailed system & development documentation
```

---

## 🚀 Getting Started (Local Development)

### 1. Prerequisite Installations
Ensure you have the following installed on your machine:
- **Java JDK 17 or higher**
- **Node.js (v18+) & npm**
- **Docker & Docker Compose**

### 2. Running the Infrastructure
Start the Kafka broker, Redis, and MySQL instances using Docker Compose:
```bash
docker-compose up -d database cache kafka
```

### 3. Starting the Backend Services
1. Run the **Eureka Server** first:
   ```bash
   cd eureka-server && ./mvnw spring-boot:run
   ```
2. Run the **Config Server**:
   ```bash
   cd config-server && ./mvnw spring-boot:run
   ```
3. Run the microservices (`auth-service`, `url-service`, `analytics-service`, `notification-service`, `api-gateway`):
   ```bash
   # Run in each respective directory
   ./mvnw spring-boot:run
   ```

### 4. Running the Frontend
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```
4. Access the web app at `http://localhost:5173`.
