# Production-Grade URL Shortener — Master Roadmap

## Project Vision

Build a **Bitly-like URL Shortener** from scratch, learning industry-standard
engineering practices across the full stack — designed for candidates preparing
for senior/staff engineering roles at large-scale tech companies.

---

## Phase Progress Tracker

| Phase | Title | Status |
|-------|-------|--------|
| 1 | Requirements Engineering | 🟡 In Progress |
| 2 | System Design (HLD + LLD) | ⬜ Pending |
| 3 | Database Design | ⬜ Pending |
| 4 | Microservices Design | ⬜ Pending |
| 5 | Backend Development | ⬜ Pending |
| 6 | Caching (Redis) | ⬜ Pending |
| 7 | Messaging (Kafka) | ⬜ Pending |
| 8 | Security | ⬜ Pending |
| 9 | Frontend (React + Vite) | ⬜ Pending |
| 10 | Testing | ⬜ Pending |
| 11 | Docker | ⬜ Pending |
| 12 | CI/CD | ⬜ Pending |
| 13 | Monitoring | ⬜ Pending |
| 14 | Production Readiness | ⬜ Pending |

---

## Phase 1 — Requirements Engineering 🟡

### Key Decisions Made
- Scale target: ~100M URLs shortened per day (Bitly-scale)
- Read-heavy system: ~10:1 read/write ratio
- Short code length: 7 characters (Base62)

### Pending Tasks for User
- [ ] Complete Functional Requirements list
- [ ] Complete Non-Functional Requirements list
- [ ] Complete Capacity Estimation worksheet
- [ ] Answer Phase 1 interview questions

---

## Tech Stack Locked

### Backend
- Java 21, Spring Boot 3.x, Spring Security, Spring Data JPA
- PostgreSQL (primary DB), Redis (cache), Kafka (events)

### Frontend
- React 18, TypeScript, Vite, React Query, Tailwind CSS

### DevOps
- Docker, Docker Compose, GitHub Actions, Kubernetes (Phase 14)

### Testing
- JUnit 5, Mockito, Testcontainers, REST Assured

### Observability
- Prometheus, Grafana, ELK Stack, OpenTelemetry

---

## Repository Structure (Target)

```
url-shortener/
├── services/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── url-service/
│   ├── analytics-service/
│   └── notification-service/
├── frontend/
├── infrastructure/
│   ├── docker/
│   ├── k8s/
│   └── monitoring/
├── .github/
│   └── workflows/
└── docs/
    ├── architecture/
    ├── api/
    └── decisions/   ← Architecture Decision Records (ADRs)
```
