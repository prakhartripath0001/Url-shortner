import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Mail, Lock, Eye, EyeOff, ArrowRight } from "lucide-react";
import { Link } from "react-router-dom";
import AuthNavbar from "../../../components/AuthNavbar/AuthNavbar.jsx";

function SignupPage() {
  const { t } = useTranslation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    // Registration handler logic will be implemented here
    console.log("Signing up with:", { email, password });
  };

  return (
    <div className="min-h-screen bg-slate-50/50">
      <AuthNavbar />
      <div className="flex items-center justify-center px-4 py-12 sm:px-6 lg:px-8">
      <div className="w-full max-w-md bg-white p-8 rounded-2xl border border-slate-100 shadow-xl shadow-slate-200/50">

        {/* Heading + Already have account */}
        <div className="mb-6">
          <h2 className="text-2xl font-bold tracking-tight text-slate-900">
            Create your account
          </h2>
          <p className="mt-1.5 text-sm text-slate-500">
            Already have an account?{" "}
            <Link
              to="/login"
              className="font-semibold text-blue-600 hover:text-blue-500 transition"
            >
              Login
            </Link>
          </p>
        </div>

        {/* Signup Form */}
        <form className="space-y-5" onSubmit={handleSubmit}>

          {/* Email Field */}
          <div>
            <label htmlFor="signup-email" className="block text-sm font-medium text-slate-700 mb-1.5">
              Email Address
            </label>
            <div className="relative">
              <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                <Mail size={18} />
              </div>
              <input
                id="signup-email"
                name="email"
                type="email"
                autoComplete="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="block w-full rounded-xl border border-slate-200 py-3 pl-10 pr-3 text-slate-900 placeholder-slate-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-100 transition sm:text-sm"
                placeholder="name@example.com"
              />
            </div>
          </div>

          {/* Password Field */}
          <div>
            <label htmlFor="signup-password" className="block text-sm font-medium text-slate-700 mb-1.5">
              Password
            </label>
            <div className="relative">
              <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                <Lock size={18} />
              </div>
              <input
                id="signup-password"
                name="password"
                type={showPassword ? "text" : "password"}
                autoComplete="new-password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="block w-full rounded-xl border border-slate-200 py-3 pl-10 pr-10 text-slate-900 placeholder-slate-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-100 transition sm:text-sm"
                placeholder="••••••••"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute inset-y-0 right-0 flex items-center pr-3 text-slate-400 hover:text-slate-600 focus:outline-none"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          {/* Create Account Button */}
          <div className="pt-1">
            <button
              type="submit"
              className="group flex w-full justify-center rounded-xl bg-blue-600 px-4 py-3 text-sm font-semibold text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-200"
            >
              <span className="flex items-center gap-1.5">
                Create a free account
                <ArrowRight size={16} className="group-hover:translate-x-0.5 transition-transform" />
              </span>
            </button>
          </div>

          {/* Terms Disclaimer */}
          <p className="text-xs text-center text-slate-400 select-none">
            By creating an account, you agree to our{" "}
            <a href="#" className="underline hover:text-slate-600 transition">Terms of Service</a>{" "}
            and{" "}
            <a href="#" className="underline hover:text-slate-600 transition">Privacy Policy</a>.
          </p>
        </form>
      </div>
      </div>
    </div>
  );
}

export default SignupPage;
