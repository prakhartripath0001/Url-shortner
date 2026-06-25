import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Mail, Lock, User, Eye, EyeOff, ArrowRight, Loader2, Zap, CheckCircle2 } from "lucide-react";
import toast from "react-hot-toast";
import { authService } from "../../../services/authService";

export default function SignupPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: "", email: "", password: "", confirmPassword: "" });
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [done, setDone] = useState(false);

  const validate = () => {
    const e = {};
    if (!form.username || form.username.length < 3) e.username = "Username must be at least 3 characters";
    if (!form.email || !/\S+@\S+\.\S+/.test(form.email)) e.email = "Enter a valid email address";
    if (!form.password || form.password.length < 8) e.password = "Password must be at least 8 characters";
    if (form.password !== form.confirmPassword) e.confirmPassword = "Passwords do not match";
    return e;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setLoading(true);
    try {
      await authService.register({ username: form.username, email: form.email, password: form.password });
      setDone(true);
    } catch (err) {
      const msg = err.response?.data?.detail || "Registration failed. Please try again.";
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

  if (done) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 flex items-center justify-center p-4">
        <div className="text-center max-w-md">
          <div className="w-20 h-20 bg-green-500/20 rounded-full flex items-center justify-center mx-auto mb-6">
            <CheckCircle2 size={40} className="text-green-400" />
          </div>
          <h2 className="text-2xl font-bold text-white mb-3">Check your email!</h2>
          <p className="text-slate-400 mb-8">
            We sent a verification link to <span className="text-indigo-400 font-medium">{form.email}</span>.
            Click it to activate your account.
          </p>
          <Link to="/login" className="inline-flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl px-6 py-3 text-sm font-semibold transition">
            <ArrowRight size={16} /> Go to Login
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 flex items-center justify-center p-4">
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-96 h-96 bg-indigo-500/20 rounded-full blur-3xl" />
        <div className="absolute -bottom-40 -left-40 w-96 h-96 bg-violet-500/20 rounded-full blur-3xl" />
      </div>

      <div className="relative w-full max-w-md">
        <div className="text-center mb-8">
          <Link to="/" className="inline-flex items-center gap-2 text-white font-bold text-2xl">
            <div className="w-9 h-9 bg-gradient-to-br from-indigo-500 to-violet-600 rounded-xl flex items-center justify-center">
              <Zap size={20} className="text-white" />
            </div>
            Shortify
          </Link>
          <p className="text-slate-400 mt-2 text-sm">Create your free account</p>
        </div>

        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8 shadow-2xl">
          <form onSubmit={handleSubmit} className="space-y-4" noValidate>
            {/* Username */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Username</label>
              <div className="relative">
                <User size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                <input id="signup-username" type="text" placeholder="johndoe" className={inputCls("username")} autoComplete="username" {...field("username")} />
              </div>
              {errors.username && <p className="text-xs text-red-400 mt-1.5">{errors.username}</p>}
            </div>

            {/* Email */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Email</label>
              <div className="relative">
                <Mail size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                <input id="signup-email" type="email" placeholder="you@example.com" className={inputCls("email")} autoComplete="email" {...field("email")} />
              </div>
              {errors.email && <p className="text-xs text-red-400 mt-1.5">{errors.email}</p>}
            </div>

            {/* Password */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Password</label>
              <div className="relative">
                <Lock size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                <input id="signup-password" type={showPw ? "text" : "password"} placeholder="Min 8 characters" className={`${inputCls("password")} pr-11`} autoComplete="new-password" {...field("password")} />
                <button type="button" onClick={() => setShowPw(v => !v)} className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-200">
                  {showPw ? <EyeOff size={17} /> : <Eye size={17} />}
                </button>
              </div>
              {errors.password && <p className="text-xs text-red-400 mt-1.5">{errors.password}</p>}
            </div>

            {/* Confirm Password */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Confirm Password</label>
              <div className="relative">
                <Lock size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                <input id="signup-confirm-password" type={showPw ? "text" : "password"} placeholder="••••••••" className={inputCls("confirmPassword")} autoComplete="new-password" {...field("confirmPassword")} />
              </div>
              {errors.confirmPassword && <p className="text-xs text-red-400 mt-1.5">{errors.confirmPassword}</p>}
            </div>

            <button
              id="signup-submit"
              type="submit"
              disabled={loading}
              className="group w-full flex items-center justify-center gap-2 rounded-xl
                         bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-500 hover:to-violet-500
                         py-3 text-sm font-semibold text-white transition-all duration-200
                         disabled:opacity-60 disabled:cursor-not-allowed shadow-lg shadow-indigo-500/25 mt-2"
            >
              {loading
                ? <Loader2 size={18} className="animate-spin" />
                : <><span>Create Account</span><ArrowRight size={16} className="group-hover:translate-x-0.5 transition-transform" /></>
              }
            </button>

            <p className="text-center text-sm text-slate-400">
              Already have an account?{" "}
              <Link to="/login" className="text-indigo-400 hover:text-indigo-300 font-medium transition">Sign in</Link>
            </p>
          </form>
        </div>
      </div>
    </div>
  );
}
