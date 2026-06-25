# Shortify — Production-Grade URL Shortener Microservices Stack

Shortify is a distributed, high-performance, Bitly-like URL shortener built on Spring Boot, React, and Apache Kafka. It features a complete microservice-based architecture, caching, event streaming, payment gateways, and containerized deployments.

---

## Architecture Overview

Shortify is structured as a collection of decoupled microservices interacting synchronously via HTTP/REST and asynchronously via Apache Kafka. All persistent state is isolated per service using the **Database-per-Service** pattern, running on **MySQL**.

### Microservice Registry

| Service | Port | Database | Primary Responsibility |
| :--- | :--- | :--- | :--- |
| **Frontend** | `3000` | None | Multilingual React dashboard (English/Hindi), charts, checkout |
| **Auth Service** | `8082` | `auth_db` | User identity, registration, session management, and JWT issuance |
| **URL Service** | `8083` | `url_db` | Core link shortening, Base62 hashing, redirect path, QR generation |
| **Analytics Service** | `8084` | `analytics_db` | Asynchronous link-click event tracking (browser, OS, country, city) |
| **Notification Service** | `8085` | None | Event-driven notifications (e.g. SMTP/Mailhog alerts) |
| **Payment Service** | `8086` | `payment_db` | Razorpay order creation, payment verification, and billing |

---

## Tech Stack

- **Frontend**: React 18, Vite, React Query, React Hot Toast, Tailwind CSS, Lucide icons, i18next (Multilingual)
- **Backend**: Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA, Hibernate, JWT, Flyway (Migration management)
- **Caching**: Redis (LRU eviction for redirect hot path)
- **Messaging**: Apache Kafka (Event streaming for click tracking and payment notifications)
- **Database**: MySQL 8.x (Separated per microservice domain)
- **Containerization & CI**: Docker, Docker Compose, GitHub Actions workflows

---

## Unified Command: Run Everything Locally

You can build and spin up the entire architecture (databases, messaging queues, caching layers, and microservices) with a single command.

### Step 1: Clone and Configure Environment

1. Copy the environment variables configuration template for the frontend:
   ```bash
   cp frontend/.env.example frontend/.env.local
   ```
2. Open `frontend/.env.local` and add your Razorpay checkout credentials if you plan to verify payments:
   ```properties
   VITE_RAZORPAY_KEY_ID=rzp_test_YOUR_KEY_HERE
   ```

### Step 2: Launch the Stack

Run the following command from the root directory:

```bash
docker compose up --build -d
```

This single command will:
1. Initialize the **MySQL** container and execute `init.sql` to dynamically create all required databases.
2. Launch **Redis** (caching) and **Zookeeper/Kafka** (event bus).
3. Build and package the Java JARs for the 5 back-end services using multi-stage builds.
4. Launch the services and wait for health check clearances.
5. Compile and serve the React **Frontend** application on port `3000`.

### Local Console Tool Port Maps
- **Frontend App**: [http://localhost:3000](http://localhost:3000)
- **Kafka Web Console**: [http://localhost:8090](http://localhost:8090)
- **phpMyAdmin (DB management)**: [http://localhost:8080](http://localhost:8080) (Log in with username `root` / password `root`)
- **MailHog (SMTP Client)**: [http://localhost:8025](http://localhost:8025)

---

## Repository Structure

```text
├── backend/
│   └── services/
│       ├── auth-service/           # User Auth, session management (Port 8082)
│       ├── url-service/            # URL redirects, Base62 hashing (Port 8083)
│       ├── analytics-service/      # Tracks clicks consumed via Kafka (Port 8084)
│       ├── notification-service/   # Event-driven mail notifications (Port 8085)
│       └── payment-service/        # Billing & Razorpay gateway (Port 8086)
├── frontend/                       # React 18 dashboard & billing portal (Port 3000)
├── infrastructure/
│   ├── docker/
│   │   ├── mysql/                  # Database schema init SQL scripts
│   │   └── monitoring/             # Prometheus & Grafana configs
│   └── monitoring/
└── docker-compose.yml              # Combined local runtime orchestrator
```

---

## Verifying Local Status

You can check service status and health-checks using docker compose:
```bash
docker compose ps
```
And view logs for any individual service (e.g. url-service):
```bash
docker compose logs -f url-service
```
