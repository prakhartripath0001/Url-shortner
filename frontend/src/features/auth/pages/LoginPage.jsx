import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Mail, Lock, Eye, EyeOff, ArrowRight, Loader2 } from "lucide-react";
import { Link } from "react-router-dom";
import AuthNavbar from "../../../components/AuthNavbar/AuthNavbar.jsx";

function LoginPage() {
  const { t } = useTranslation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};
    if (!email) newErrors.email = "Email is required.";
    else if (!/\S+@\S+\.\S+/.test(email)) newErrors.email = "Enter a valid email address.";
    if (!password) newErrors.password = "Password is required.";
    else if (password.length < 8) newErrors.password = "Password must be at least 8 characters.";
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }
    setErrors({});
    setIsLoading(true);
    try {
      // API call will be implemented here
      await new Promise((r) => setTimeout(r, 1000)); // placeholder
      console.log("Logging in with:", { email, password });
    } finally {
      setIsLoading(false);
    }
  };

  const inputClass = (field) =>
    `block w-full rounded-xl border py-3 pl-10 pr-3 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 transition sm:text-sm ${
      errors[field]
        ? "border-red-400 focus:border-red-400 focus:ring-red-100"
        : "border-slate-200 focus:border-blue-500 focus:ring-blue-100"
    }`;

  return (
    <div className="min-h-screen bg-slate-50/50">
      <AuthNavbar />
      <div className="flex items-center justify-center px-4 py-12 sm:px-6 lg:px-8">
        <div className="w-full max-w-md space-y-8 bg-white p-8 rounded-2xl border border-slate-100 shadow-xl shadow-slate-200/50">

          {/* Heading */}
          <div className="text-center">
            <h2 className="text-2xl font-bold tracking-tight text-slate-900">
              {t("login")}
            </h2>
            <p className="mt-2 text-sm text-slate-500">
              {t("login_subtitle")}
            </p>
          </div>

          {/* Login Form */}
          <form className="mt-8 space-y-5" onSubmit={handleSubmit} noValidate>

            {/* Email Field */}
            <div>
              <label htmlFor="login-email" className="block text-sm font-medium text-slate-700 mb-1.5">
                {t("email_label")}
              </label>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <Mail size={18} />
                </div>
                <input
                  id="login-email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  value={email}
                  onChange={(e) => { setEmail(e.target.value); setErrors((prev) => ({ ...prev, email: undefined })); }}
                  className={inputClass("email")}
                  placeholder={t("email_placeholder")}
                />
              </div>
              {errors.email && <p className="mt-1.5 text-xs text-red-500">{errors.email}</p>}
            </div>

            {/* Password Field */}
            <div>
              <label htmlFor="login-password" className="block text-sm font-medium text-slate-700 mb-1.5">
                {t("password_label")}
              </label>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <Lock size={18} />
                </div>
                <input
                  id="login-password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  autoComplete="current-password"
                  value={password}
                  onChange={(e) => { setPassword(e.target.value); setErrors((prev) => ({ ...prev, password: undefined })); }}
                  className={`${inputClass("password")} pr-10`}
                  placeholder={t("password_placeholder")}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 flex items-center pr-3 text-slate-400 hover:text-slate-600 focus:outline-none"
                  aria-label={showPassword ? "Hide password" : "Show password"}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
              {errors.password && <p className="mt-1.5 text-xs text-red-500">{errors.password}</p>}
            </div>

            {/* Forgot Password */}
            <div className="flex items-center justify-end">
              <Link to="/forgot-password" className="text-sm font-semibold text-blue-600 hover:text-blue-500 transition">
                {t("forgot_password")}
              </Link>
            </div>

            {/* Login Button */}
            <button
              type="submit"
              disabled={isLoading}
              className="group relative flex w-full justify-center rounded-xl bg-blue-600 px-4 py-3 text-sm font-semibold text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-200 disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {isLoading ? (
                <Loader2 size={18} className="animate-spin" />
              ) : (
                <span className="flex items-center gap-1.5">
                  {t("login")}
                  <ArrowRight size={16} className="group-hover:translate-x-0.5 transition-transform" />
                </span>
              )}
            </button>

            {/* Terms Disclaimer */}
            <p className="text-xs text-center text-slate-400 select-none">
              {t("terms_login")}
            </p>

            {/* Signup prompt */}
            <p className="text-sm text-center text-slate-500">
              {t("no_account")}{" "}
              <Link to="/signup" className="font-semibold text-blue-600 hover:text-blue-500 transition">
                {t("signup")}
              </Link>
            </p>
          </form>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
