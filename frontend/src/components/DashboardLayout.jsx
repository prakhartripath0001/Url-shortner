// src/components/DashboardLayout.jsx
import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  Zap, LayoutDashboard, BarChart2, Crown, Settings,
  LogOut, Menu, X, Bell, ChevronDown
} from "lucide-react";
import { useAuthStore } from "../store/authStore";
import { authService } from "../services/authService";
import toast from "react-hot-toast";

const NAV = [
  { label: "Dashboard", href: "/dashboard", icon: LayoutDashboard },
  { label: "Analytics", href: "/analytics", icon: BarChart2 },
  { label: "Upgrade", href: "/pricing", icon: Crown },
  { label: "Settings", href: "/settings", icon: Settings },
];

export default function DashboardLayout({ children }) {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const [sidebar, setSidebar] = useState(false);
  const { user, refreshToken, logout } = useAuthStore();

  const handleLogout = async () => {
    try {
      await authService.logout(refreshToken);
    } catch {/* ignore */} finally {
      logout();
      navigate("/login");
      toast.success("Logged out successfully");
    }
  };

  const NavItem = ({ href, icon: Icon, label }) => {
    const active = pathname === href || pathname.startsWith(href + "/");
    return (
      <Link
        to={href}
        onClick={() => setSidebar(false)}
        className={`flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm font-medium transition-all
          ${active
            ? "bg-indigo-500/20 text-indigo-400 border border-indigo-500/20"
            : "text-slate-400 hover:text-white hover:bg-white/5"
          }`}
      >
        <Icon size={18} />
        {label}
        {label === "Upgrade" && (
          <span className="ml-auto text-xs bg-gradient-to-r from-amber-400 to-orange-400 text-black rounded-full px-2 py-0.5 font-bold">
            PRO
          </span>
        )}
      </Link>
    );
  };

  const Sidebar = () => (
    <aside className="flex flex-col h-full w-64 bg-slate-900/95 border-r border-white/5 p-5">
      {/* Logo */}
      <Link to="/dashboard" className="flex items-center gap-2 mb-8 px-2">
        <div className="w-8 h-8 bg-gradient-to-br from-indigo-500 to-violet-600 rounded-lg flex items-center justify-center">
          <Zap size={18} className="text-white" />
        </div>
        <span className="text-white font-bold text-lg">Shortify</span>
      </Link>

      {/* Nav */}
      <nav className="flex-1 space-y-1">
        {NAV.map(item => <NavItem key={item.href} {...item} />)}
      </nav>

      {/* User */}
      <div className="mt-auto pt-4 border-t border-white/5">
        <div className="flex items-center gap-3 px-2 mb-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-indigo-500 to-violet-600 flex items-center justify-center text-white font-bold text-sm">
            {user?.username?.[0]?.toUpperCase() || "U"}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-white text-sm font-medium truncate">{user?.username}</p>
            <p className="text-slate-500 text-xs truncate">{user?.email}</p>
          </div>
        </div>
        <button
          onClick={handleLogout}
          className="w-full flex items-center gap-3 px-4 py-2.5 rounded-xl text-slate-400 hover:text-red-400 hover:bg-red-500/10 text-sm font-medium transition-all"
        >
          <LogOut size={16} /> Sign Out
        </button>
      </div>
    </aside>
  );

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-indigo-950 flex">
      {/* Desktop sidebar */}
      <div className="hidden lg:flex flex-col">
        <Sidebar />
      </div>

      {/* Mobile sidebar overlay */}
      {sidebar && (
        <div className="lg:hidden fixed inset-0 z-50 flex">
          <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={() => setSidebar(false)} />
          <div className="relative z-10 flex flex-col">
            <Sidebar />
          </div>
        </div>
      )}

      {/* Main */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Top bar */}
        <header className="h-16 flex items-center gap-4 px-6 border-b border-white/5 bg-slate-950/50 backdrop-blur-sm">
          <button
            className="lg:hidden p-2 rounded-xl text-slate-400 hover:text-white hover:bg-white/10 transition"
            onClick={() => setSidebar(true)}
          >
            <Menu size={20} />
          </button>
          <div className="flex-1" />
          <button className="relative p-2 rounded-xl text-slate-400 hover:text-white hover:bg-white/10 transition">
            <Bell size={20} />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-indigo-500 rounded-full" />
          </button>
          <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-indigo-500 to-violet-600 flex items-center justify-center text-white font-bold text-sm">
            {user?.username?.[0]?.toUpperCase() || "U"}
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 p-6 lg:p-8 overflow-auto">
          {children}
        </main>
      </div>
    </div>
  );
}
