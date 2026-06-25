import { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { Mail, Lock, Eye, EyeOff, ArrowRight, Loader2, Zap } from "lucide-react";
import toast from "react-hot-toast";
import { authService } from "../../../services/authService";
import { useAuthStore } from "../../../store/authStore";

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const setAuth = useAuthStore((s) => s.setAuth);
  const from = location.state?.from?.pathname || "/dashboard";

  const [form, setForm] = useState({ usernameOrEmail: "", password: "" });
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const e = {};
    if (!form.usernameOrEmail) e.usernameOrEmail = "Email or username is required";
    if (!form.password) e.password = "Password is required";
    return e;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setLoading(true);
    try {
      const { data } = await authService.login(form);
      setAuth(data.user, data.accessToken, data.refreshToken);
      toast.success(`Welcome back, ${data.user.username}! 👋`);
      navigate(from, { replace: true });
    } catch (err) {
      const msg = err.response?.data?.detail || "Invalid credentials. Please try again.";
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const field = (name) => ({
    value: form[name],
    onChange: (e) => { setForm(p => ({ ...p, [name]: e.target.value })); setErrors(p => ({ ...p, [name]: null })); },
  });

  const inputCls = (name) =>
    `w-full rounded-xl border bg-white/50 py-3 pl-11 pr-4 text-sm text-slate-800 placeholder-slate-400 
     focus:outline-none focus:ring-2 transition-all
     ${errors[name] ? "border-red-400 focus:ring-red-100" : "border-slate-200 focus:border-indigo-400 focus:ring-indigo-100"}`;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 flex items-center justify-center p-4">
      {/* Background orbs */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-96 h-96 bg-indigo-500/20 rounded-full blur-3xl" />
        <div className="absolute -bottom-40 -left-40 w-96 h-96 bg-violet-500/20 rounded-full blur-3xl" />
      </div>

      <div className="relative w-full max-w-md">
        {/* Logo */}
        <div className="text-center mb-8">
          <Link to="/" className="inline-flex items-center gap-2 text-white font-bold text-2xl">
            <div className="w-9 h-9 bg-gradient-to-br from-indigo-500 to-violet-600 rounded-xl flex items-center justify-center">
              <Zap size={20} className="text-white" />
            </div>
            Shortify
          </Link>
          <p className="text-slate-400 mt-2 text-sm">Sign in to your account</p>
        </div>

        {/* Card */}
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8 shadow-2xl">
          <form onSubmit={handleSubmit} className="space-y-5" noValidate>
            {/* Email / Username */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Email or Username</label>
              <div className="relative">
                <Mail size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                <input
                  id="login-email"
                  type="text"
                  placeholder="you@example.com"
                  className={inputCls("usernameOrEmail")}
                  autoComplete="username"
                  {...field("usernameOrEmail")}
                />
              </div>
              {errors.usernameOrEmail && <p className="text-xs text-red-400 mt-1.5">{errors.usernameOrEmail}</p>}
            </div>

            {/* Password */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Password</label>
              <div className="relative">
                <Lock size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                <input
                  id="login-password"
                  type={showPw ? "text" : "password"}
                  placeholder="••••••••"
                  className={`${inputCls("password")} pr-11`}
                  autoComplete="current-password"
                  {...field("password")}
                />
                <button type="button" onClick={() => setShowPw(v => !v)}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-200">
                  {showPw ? <EyeOff size={17} /> : <Eye size={17} />}
                </button>
              </div>
              {errors.password && <p className="text-xs text-red-400 mt-1.5">{errors.password}</p>}
            </div>

            {/* Forgot */}
            <div className="text-right">
              <Link to="/forgot-password" className="text-xs text-indigo-400 hover:text-indigo-300 transition">
                Forgot password?
              </Link>
            </div>

            {/* Submit */}
            <button
              id="login-submit"
              type="submit"
              disabled={loading}
              className="group w-full flex items-center justify-center gap-2 rounded-xl 
                         bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-500 hover:to-violet-500
                         py-3 text-sm font-semibold text-white transition-all duration-200
                         focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 focus:ring-offset-transparent
                         disabled:opacity-60 disabled:cursor-not-allowed shadow-lg shadow-indigo-500/25"
            >
              {loading
                ? <Loader2 size={18} className="animate-spin" />
                : <><span>Sign In</span><ArrowRight size={16} className="group-hover:translate-x-0.5 transition-transform" /></>
              }
            </button>

            <p className="text-center text-sm text-slate-400">
              Don't have an account?{" "}
              <Link to="/signup" className="text-indigo-400 hover:text-indigo-300 font-medium transition">Sign up free</Link>
            </p>
          </form>
        </div>
      </div>
    </div>
  );
}
