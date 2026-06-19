import { Routes, Route, useLocation } from "react-router-dom";
import { useEffect } from "react";
import HomePage from "./pages/HomePage.jsx";
import LoginPage from "./features/auth/pages/LoginPage.jsx";
import SignupPage from "./features/auth/pages/SignupPage.jsx";
import ForgotPasswordPage from "./features/auth/pages/ForgotPasswordPage.jsx";
import NotFoundPage from "./pages/NotFoundPage.jsx";

const PAGE_TITLES = {
  "/": "Shortify — Shorten, Share, Track",
  "/login": "Login — Shortify",
  "/signup": "Sign Up — Shortify",
  "/forgot-password": "Reset Password — Shortify",
};

function TitleManager() {
  const location = useLocation();
  useEffect(() => {
    document.title = PAGE_TITLES[location.pathname] || "Shortify";
  }, [location.pathname]);
  return null;
}

function App() {
  return (
    <>
      <TitleManager />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </>
  );
}

export default App;