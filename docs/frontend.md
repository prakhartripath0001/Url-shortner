# Frontend Documentation - Shortify

The Shortify client web interface is built using **React 19**, **Vite** (for fast bundling/HMR), **Tailwind CSS** (for responsive styles), **React Router v6** (for page navigation), and **i18next** (for internationalization).

---

## Tech Stack

| Technology | Version | Purpose |
| :--- | :--- | :--- |
| React | 19 | UI component library |
| Vite | 8 | Dev server and module bundler |
| Tailwind CSS | 3.4 | Utility-first styling |
| React Router | 6 | Client-side page routing |
| i18next | 26 | Internationalization (EN / HI) |
| Lucide React | 1.20 | SVG icon components |

---

## Directory Structure

```text
frontend/src/
├── components/
│   ├── Navbar/
│   │   └── Navbar.jsx           # Full navbar with links — shown on HomePage only
│   ├── AuthNavbar/
│   │   └── AuthNavbar.jsx       # Logo-only navbar — shown on Login and Signup pages
│   └── LanguageSwitcher/
│       └── LanguageSwitcher.jsx # Language toggle component (placeholder)
├── features/
│   └── auth/
│       └── pages/
│           ├── LoginPage.jsx    # Login form page
│           └── SignupPage.jsx   # Signup/registration form page
├── pages/
│   └── HomePage.jsx             # Landing page with full Navbar
├── i18n/
│   └── i18n.js                  # i18next initialization
├── locales/
│   ├── en/translation.json      # English translation strings
│   └── hi/translation.json      # Hindi translation strings
├── App.jsx                      # Root router — maps paths to page components
├── main.jsx                     # App entry point — BrowserRouter + i18n bootstrap
└── index.css                    # Global CSS + Tailwind directives + Poppins font
```

---

## Routing

Routes are defined in [App.jsx](../frontend/src/App.jsx) using React Router:

| Path | Component | Description / Access |
| :--- | :--- | :--- |
| `/` | `HomePage` | Public landing page with features overview |
| `/login` | `LoginPage` | Auth access portal |
| `/signup` | `SignupPage` | User registration |
| `/pricing` | `PricingPage` | Subscription plan list and Razorpay checkout modal |
| `/docs` | `DocsPage` | Microservices API reference docs and endpoints map |
| `/dashboard` | `DashboardPage` | Private user dashboard (URL lists, metrics summary, creation form, QR viewer) |
| `/analytics/:shortCode` | `AnalyticsPage` | Deep link performance breakdown (OS, device types, top countries) |
| `/settings` | `SettingsPage` | User profile configs, subscription records, and security toggles |

---

## Pages

### HomePage (`/`)
- Displays the full landing navbar.
- Hero section with "Get Started Free", demo shortening text box, and feature highlight grid.

### LoginPage (`/login`)
- Displays authentication navbar with logo linking to home.
- Form inputs for email, password, showing password toggles, and direct routing back to dashboard upon success.

### SignupPage (`/signup`)
- Accounts setup portal with basic validations.

### PricingPage (`/pricing`)
- Premium billing layout showcasing Free, Pro, and Business tiers.
- Integrated with Razorpay JS SDK to generate orders, trigger safe payment overlays, and report transaction hashes back to `/api/v1/payments/verify`.

### DocsPage (`/docs`)
- Dedicated visual guide for the distributed APIs layout.
- Provides interactive examples of headers, payloads, and response JSON objects for Auth, URLs, QR, and Analytics services.

### DashboardPage (`/dashboard`)
- Direct access to URLs inventory, paginated search, and creation forms.
- Features dynamic copy utilities and a **QR Code** viewer action button linked directly to the backend's `/api/v1/urls/{shortCode}/qr` generator.

### AnalyticsPage (`/analytics/:shortCode`)
- Renders responsive Recharts area and pie graphs plotting click counts, country distributions, and device specifications.

---

## Internationalization (i18n)

Configured to support English (`en`) and Hindi (`hi`), defaulting to English.

### Adding New Language Strings
Insert key-value pairs into both translation files:
- [en/translation.json](../frontend/src/locales/en/translation.json)
- [hi/translation.json](../frontend/src/locales/hi/translation.json)

Example:
```json
{
  "welcome": "Welcome to Shortify"
}
```

### Usage inside components:
```jsx
import { useTranslation } from "react-i18next";

function MyComponent() {
  const { t } = useTranslation();
  return <h1>{t("welcome")}</h1>;
}
```

---

## Startup Script

```bash
cd frontend
npm install     # first time only
npm run dev     # starts dev server at http://localhost:5173
```

---

## Troubleshooting

> [!WARNING]
> **Blank Page on Startup**: If the browser shows a blank page, check the browser console. The most common cause is empty `translation.json` files (0 bytes). Both locale files must contain at least `{}` to be valid JSON. Both files are currently initialized correctly.

