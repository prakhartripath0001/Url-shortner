# Shortify - URL Shortener Application

Shortify is a URL Shortener application currently in active development. It has a multilingual React frontend with authentication pages and a Spring Boot auth-service backend scaffold.

---

## Table of Contents
- [Planned System Architecture](docs/architecture.md)
- [Auth Service Architecture](docs/Backend/auth-architecture.md)
- [Frontend Documentation](docs/frontend.md)
- [References & Acknowledgements](docs/REFERENCES.md)

---

## Current State

### Frontend (Complete)
- React 19 + Vite + Tailwind CSS
- React Router v6 for page navigation
- i18next for English / Hindi internationalization
- Home page with full Navbar (Login & Sign up buttons)
- Login page — email, password, forgot password, terms disclaimer, link to Signup
- Signup page — email, password, create account button, link to Login, terms disclaimer
- Minimal AuthNavbar on auth pages (logo only, links back to home)

### Backend - Auth Service (In Progress)
- Spring Boot 3.x scaffold initialized at `backend/services/auth-service/`
- Java 17, Spring Security, Spring Data JPA, Validation, Lombok
- MySQL database driver and Flyway for migration management
- jjwt (0.12.7) for JWT token generation
- Spring Boot Actuator configured
- Flyway database migrations created:
  - `V1` — `users` table
  - `V2` — `refresh_tokens` table
  - `V3` — `sessions` table
  - `V4` — `audit_logs` table
  - `V5` — `email_verification_tokens` table
  - `V6` — `password_reset_tokens` table

---

## Repository Structure

```text
├── backend/
│   └── services/
│       └── auth-service/       # Spring Boot 3 Auth Service (Port 8082)
│           ├── src/main/java/  # Application source code
│           └── src/main/resources/db/migration/  # Flyway SQL migrations
├── frontend/                   # React 19 + Vite + Tailwind web app
│   └── src/
│       ├── components/         # Navbar, AuthNavbar, LanguageSwitcher
│       ├── features/auth/pages/ # LoginPage, SignupPage
│       ├── pages/              # HomePage
│       ├── locales/            # en & hi translation JSON files
│       └── i18n/               # i18next configuration
└── docs/                       # System and development documentation
    └── Backend/                # Backend-specific architecture docs
```

---

## Getting Started (Local Development)

### Running the Frontend
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
