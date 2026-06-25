import { Routes, Route, useLocation } from "react-router-dom";
import { useEffect, lazy, Suspense } from "react";
import { Loader2 } from "lucide-react";
import ProtectedRoute from "./components/ProtectedRoute";

// Lazy load pages for code splitting (faster initial load)
const HomePage = lazy(() => import("./pages/HomePage"));
const LoginPage = lazy(() => import("./features/auth/pages/LoginPage"));
const SignupPage = lazy(() => import("./features/auth/pages/SignupPage"));
const ForgotPasswordPage = lazy(() => import("./features/auth/pages/ForgotPasswordPage"));
const DashboardPage = lazy(() => import("./pages/DashboardPage"));
const AnalyticsPage = lazy(() => import("./pages/AnalyticsPage"));
const PricingPage = lazy(() => import("./pages/PricingPage"));
const SettingsPage = lazy(() => import("./pages/SettingsPage"));
const NotFoundPage = lazy(() => import("./pages/NotFoundPage"));

const PAGE_TITLES = {
  "/": "Shortify — Shorten, Share, Track",
  "/login": "Sign In — Shortify",
  "/signup": "Create Account — Shortify",
  "/forgot-password": "Reset Password — Shortify",
  "/dashboard": "Dashboard — Shortify",
  "/analytics": "Analytics — Shortify",
  "/pricing": "Pricing — Shortify",
  "/settings": "Settings — Shortify",
};

function TitleManager() {
  const location = useLocation();
  useEffect(() => {
    const title = PAGE_TITLES[location.pathname] ||
      (location.pathname.startsWith("/analytics/") ? "Analytics — Shortify" : "Shortify");
    document.title = title;
  }, [location.pathname]);
  return null;
}

const PageLoader = () => (
  <div className="min-h-screen bg-slate-950 flex items-center justify-center">
    <Loader2 size={32} className="animate-spin text-indigo-400" />
  </div>
);

export default function App() {
  return (
    <>
      <TitleManager />
      <Suspense fallback={<PageLoader />}>
        <Routes>
          {/* Public */}
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/pricing" element={<PricingPage />} />

          {/* Protected — requires login */}
          <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
          <Route path="/analytics/:shortCode" element={<ProtectedRoute><AnalyticsPage /></ProtectedRoute>} />
          <Route path="/analytics" element={<ProtectedRoute><AnalyticsPage /></ProtectedRoute>} />
          <Route path="/settings" element={<ProtectedRoute><SettingsPage /></ProtectedRoute>} />

          {/* 404 */}
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Suspense>
    </>
  );
}