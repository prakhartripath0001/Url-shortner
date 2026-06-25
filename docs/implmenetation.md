# Production-Grade URL Shortener — Master Roadmap

## Project Vision

Build a **Bitly-like URL Shortener** from scratch, learning industry-standard
engineering practices across the full stack — designed for candidates preparing
for senior/staff engineering roles at large-scale tech companies.

---

## Phase Progress Tracker

| Phase | Title | Status |
|-------|-------|--------|
| 1 | Requirements Engineering | ✅ Complete |
| 2 | System Design (HLD + LLD) | ✅ Complete |
| 3 | Database Design | ✅ Complete |
| 4 | Microservices Design | ✅ Complete |
| 5 | Backend Development | ✅ Complete |
| 6 | Caching (Redis) | ✅ Complete |
| 7 | Messaging (Kafka) | ✅ Complete |
| 8 | Security | ✅ Complete |
| 9 | Frontend (React + Vite) | ✅ Complete |
| 10 | Testing | ✅ Complete |
| 11 | Docker | ✅ Complete |
| 12 | CI/CD | ✅ Complete |
| 13 | Monitoring | 🟡 In Progress |
| 14 | Production Readiness | ⬜ Pending |

---

## Tech Stack Locked

### Backend
- Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA
- MySQL (primary DB), Redis (cache), Kafka (events)

### Frontend
- React 18, Vite, React Query, Tailwind CSS, i18next

### DevOps
- Docker, Docker Compose, GitHub Actions, Kubernetes (Phase 14)

### Testing
- JUnit 5, Mockito, Testcontainers

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
