# Frontend Documentation - Shortify

The Shortify client web interface is built using **React 19**, **Vite** (for fast bundling/HMR), **Tailwind CSS** (for responsive styles), and **i18next** (for internationalization).

---

## 🛠️ Architecture & Setup

### Core Technologies
* **React 19**: Modern UI component library.
* **Vite**: Dev server and module bundler.
* **Tailwind CSS**: Utility-first CSS framework for responsive layout design.
* **i18next**: Localization library.
* **Lucide React**: Clean and customizable SVG icons.

### Startup Script
From the `frontend` directory:
```bash
# Install NPM dependencies
npm install

# Start Vite HMR Dev Server
npm run dev

# Compile Production Build (dist/ folder)
npm run build
```
By default, the application runs on **`http://localhost:5173`**.

---

## 📁 Key Frontend Directories

```text
frontend/src/
├── assets/          # Logo SVGs, images, static assets
├── components/      # Reusable UI widgets (Navbar, LanguageSwitcher)
├── i18n/            # Internationalization config core file
├── locales/         # translation.json dictionaries (English & Hindi)
├── pages/           # Page routes (Dashboard, Landing page, 404, etc.)
├── App.css          # Styling updates
├── index.css        # Global CSS + Tailwind directives + Poppins font import
├── App.jsx          # Root routing and main layout structure
└── main.jsx         # App mounting point & i18n initializer
```

---

## 🌐 Internationalization (i18n)

We support multi-language localizations. The active dictionaries are loaded from `src/locales/`.

### Config File (`src/i18n/i18n.js`)
Configured to support English (`en`) and Hindi (`hi`), defaulting to English:
```javascript
import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import en from "../locales/en/translation.json";
import hi from "../locales/hi/translation.json";

i18n.use(initReactI18next).init({
  resources: {
    en: { translation: en },
    hi: { translation: hi }
  },
  lng: "en",
  fallbackLng: "en",
  interpolation: { escapeValue: false }
});
```

### Adding New Language Strings
Insert key-value pairs into translation files:
- [English translation.json](file:///Users/prakhartripathi/Documents/Url-shortner/frontend/src/locales/en/translation.json)
- [Hindi translation.json](file:///Users/prakhartripathi/Documents/Url-shortner/frontend/src/locales/hi/translation.json)

Example:
```json
{
  "welcome": "Welcome to Shortify",
  "shorten_btn": "Shorten URL"
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

## 🔧 Troubleshooting

### Blank Page / Crash on Startup
> [!WARNING]
> **Issue**: Vite dev server loads but displays a completely blank page. Looking at the browser console displays a JSON parsing or syntax error.
> 
> **Cause**: This happens when [translation.json](file:///Users/prakhartripathi/Documents/Url-shortner/frontend/src/locales/en/translation.json) files are empty (0 bytes). Vite fails to import empty text content as JSON.
> 
> **Solution**: The locale JSON files must at least contain an empty JSON object: `{}`. We have initialized both translation files with `{}` to prevent this crash.
