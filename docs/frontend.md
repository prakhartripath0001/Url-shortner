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

Routes are defined in [App.jsx](../frontend/src/App.jsx) using React Router v6:

| Path | Component | Navbar |
| :--- | :--- | :--- |
| `/` | `HomePage` | Full Navbar (Platform, Pricing, Login, Sign up) |
| `/login` | `LoginPage` | AuthNavbar (Logo only, links to `/`) |
| `/signup` | `SignupPage` | AuthNavbar (Logo only, links to `/`) |

---

## Pages

### HomePage (`/`)
- Displays the full `Navbar` component.
- Hero section with "Get Started Free" and "Login" call-to-action buttons.

### LoginPage (`/login`)
- Displays the `AuthNavbar` with the Shortify logo linking to home.
- Email and password input fields with icons and password visibility toggle.
- Forgot password link.
- Login submit button.
- Terms disclaimer: *By logging in, you are accepting all the terms & conditions.*
- "Don't have an account? Sign up" link to `/signup`.

### SignupPage (`/signup`)
- Displays the `AuthNavbar` with the Shortify logo linking to home.
- "Create your account" heading with "Already have an account? Login" link to `/login`.
- Email and password input fields with icons and password visibility toggle.
- "Create a free account" submit button.
- Terms disclaimer: *By creating an account, you agree to our Terms of Service and Privacy Policy.*

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

