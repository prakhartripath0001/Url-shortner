import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Mail, ArrowRight, Loader2 } from "lucide-react";
import { Link } from "react-router-dom";
import AuthNavbar from "../../../components/AuthNavbar/AuthNavbar.jsx";

function ForgotPasswordPage() {
  const { t } = useTranslation();
  const [email, setEmail] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [sent, setSent] = useState(false);

  useEffect(() => {
    document.title = "Reset Password — Shortify";
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !/\S+@\S+\.\S+/.test(email)) {
      setError("Enter a valid email address.");
      return;
    }
    setError("");
    setIsLoading(true);
    try {
      // API call will be implemented here
      await new Promise((r) => setTimeout(r, 1000));
      setSent(true);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50/50">
      <AuthNavbar />
      <div className="flex items-center justify-center px-4 py-12 sm:px-6 lg:px-8">
        <div className="w-full max-w-md bg-white p-8 rounded-2xl border border-slate-100 shadow-xl shadow-slate-200/50">
          {sent ? (
            <div className="text-center">
              <div className="text-5xl mb-4">📬</div>
              <h2 className="text-xl font-bold text-slate-900 mb-2">Check your inbox</h2>
              <p className="text-sm text-slate-500 mb-6">
                We sent a reset link to <strong>{email}</strong>.
              </p>
              <Link to="/login" className="font-semibold text-blue-600 hover:text-blue-500 transition text-sm">
                {t("back_to_login")}
              </Link>
            </div>
          ) : (
            <>
              <div className="mb-6">
                <h2 className="text-2xl font-bold tracking-tight text-slate-900">
                  {t("forgot_password_heading")}
                </h2>
                <p className="mt-1.5 text-sm text-slate-500">{t("forgot_password_subtitle")}</p>
              </div>
              <form className="space-y-5" onSubmit={handleSubmit} noValidate>
                <div>
                  <label htmlFor="reset-email" className="block text-sm font-medium text-slate-700 mb-1.5">
                    {t("email_label")}
                  </label>
                  <div className="relative">
                    <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                      <Mail size={18} />
                    </div>
                    <input
                      id="reset-email"
                      type="email"
                      autoComplete="email"
                      value={email}
                      onChange={(e) => { setEmail(e.target.value); setError(""); }}
                      className={`block w-full rounded-xl border py-3 pl-10 pr-3 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 transition sm:text-sm ${
                        error
                          ? "border-red-400 focus:border-red-400 focus:ring-red-100"
                          : "border-slate-200 focus:border-blue-500 focus:ring-blue-100"
                      }`}
                      placeholder={t("email_placeholder")}
                    />
                  </div>
                  {error && <p className="mt-1.5 text-xs text-red-500">{error}</p>}
                </div>
                <button
                  type="submit"
                  disabled={isLoading}
                  className="group flex w-full justify-center rounded-xl bg-blue-600 px-4 py-3 text-sm font-semibold text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-200 disabled:opacity-60 disabled:cursor-not-allowed"
                >
                  {isLoading ? (
                    <Loader2 size={18} className="animate-spin" />
                  ) : (
                    <span className="flex items-center gap-1.5">
                      {t("send_reset_link")}
                      <ArrowRight size={16} className="group-hover:translate-x-0.5 transition-transform" />
                    </span>
                  )}
                </button>
                <p className="text-sm text-center text-slate-500">
                  <Link to="/login" className="font-semibold text-blue-600 hover:text-blue-500 transition">
                    {t("back_to_login")}
                  </Link>
                </p>
              </form>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default ForgotPasswordPage;
