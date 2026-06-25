import { useState } from "react";
import { useAuthStore } from "../../store/authStore";
import { authService } from "../../services/authService";
import { useMutation } from "@tanstack/react-query";
import { User, Mail, Lock, Eye, EyeOff, Loader2, Trash2, Crown } from "lucide-react";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import DashboardLayout from "../../components/DashboardLayout";

export default function SettingsPage() {
  const { user, updateUser, logout } = useAuthStore();
  const [activeTab, setActiveTab] = useState("profile");
  const [profileForm, setProfileForm] = useState({ username: user?.username || "" });
  const [pwForm, setPwForm] = useState({ currentPassword: "", newPassword: "", confirmPassword: "" });
  const [showPw, setShowPw] = useState({});

  const profileMutation = useMutation({
    mutationFn: (data) => authService.updateProfile(data).then(r => r.data),
    onSuccess: (data) => { updateUser(data); toast.success("Profile updated!"); },
    onError: () => toast.error("Failed to update profile"),
  });

  const passwordMutation = useMutation({
    mutationFn: (data) => authService.changePassword(data),
    onSuccess: () => { toast.success("Password changed!"); setPwForm({ currentPassword: "", newPassword: "", confirmPassword: "" }); },
    onError: (err) => toast.error(err.response?.data?.detail || "Failed to change password"),
  });

  const tabs = [
    { id: "profile", label: "Profile" },
    { id: "security", label: "Security" },
    { id: "subscription", label: "Subscription" },
    { id: "danger", label: "Danger Zone" },
  ];

  return (
    <DashboardLayout>
      <h1 className="text-2xl font-bold text-white mb-8">Settings</h1>

      <div className="flex flex-col lg:flex-row gap-8 max-w-4xl">
        {/* Tabs */}
        <div className="flex lg:flex-col gap-1 lg:w-52 flex-shrink-0">
          {tabs.map(tab => (
            <button key={tab.id} onClick={() => setActiveTab(tab.id)}
              className={`px-4 py-2.5 rounded-xl text-sm font-medium text-left transition
                ${activeTab === tab.id
                  ? "bg-indigo-500/20 text-indigo-400 border border-indigo-500/20"
                  : "text-slate-400 hover:text-white hover:bg-white/5"
                } ${tab.id === "danger" ? "text-red-400 hover:text-red-300 hover:bg-red-500/10" : ""}`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Content */}
        <div className="flex-1 bg-white/5 border border-white/10 rounded-2xl p-7">
          {/* Profile Tab */}
          {activeTab === "profile" && (
            <div>
              <h2 className="text-lg font-bold text-white mb-6">Profile Information</h2>
              <div className="flex items-center gap-5 mb-8">
                <div className="w-20 h-20 rounded-2xl bg-gradient-to-br from-indigo-500 to-violet-600 flex items-center justify-center text-white text-3xl font-bold">
                  {user?.username?.[0]?.toUpperCase()}
                </div>
                <div>
                  <p className="text-white font-semibold">{user?.username}</p>
                  <p className="text-slate-400 text-sm">{user?.email}</p>
                  <span className="inline-flex items-center gap-1 mt-1 text-xs bg-indigo-500/20 text-indigo-400 border border-indigo-500/20 rounded-full px-2 py-0.5">
                    Free Plan
                  </span>
                </div>
              </div>

              <form onSubmit={(e) => { e.preventDefault(); profileMutation.mutate(profileForm); }} className="space-y-5">
                <div>
                  <label className="block text-sm font-medium text-slate-400 mb-2">Username</label>
                  <div className="relative">
                    <User size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
                    <input
                      type="text"
                      value={profileForm.username}
                      onChange={(e) => setProfileForm(p => ({ ...p, username: e.target.value }))}
                      className="w-full bg-white/5 border border-white/10 rounded-xl pl-11 pr-4 py-2.5 text-sm text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/30 transition"
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-400 mb-2">Email</label>
                  <div className="relative">
                    <Mail size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
                    <input type="email" value={user?.email} disabled
                      className="w-full bg-white/5 border border-white/10 rounded-xl pl-11 pr-4 py-2.5 text-sm text-slate-500 cursor-not-allowed" />
                  </div>
                  <p className="text-xs text-slate-600 mt-1">Email cannot be changed</p>
                </div>
                <button type="submit" disabled={profileMutation.isPending}
                  className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl px-5 py-2.5 text-sm font-semibold transition disabled:opacity-60">
                  {profileMutation.isPending ? <Loader2 size={16} className="animate-spin" /> : null}
                  Save Changes
                </button>
              </form>
            </div>
          )}

          {/* Security Tab */}
          {activeTab === "security" && (
            <div>
              <h2 className="text-lg font-bold text-white mb-6">Change Password</h2>
              <form onSubmit={(e) => { e.preventDefault(); passwordMutation.mutate(pwForm); }} className="space-y-5 max-w-sm">
                {[
                  { key: "currentPassword", label: "Current Password", placeholder: "••••••••" },
                  { key: "newPassword", label: "New Password", placeholder: "Min 8 characters" },
                  { key: "confirmPassword", label: "Confirm New Password", placeholder: "••••••••" },
                ].map(({ key, label, placeholder }) => (
                  <div key={key}>
                    <label className="block text-sm font-medium text-slate-400 mb-2">{label}</label>
                    <div className="relative">
                      <Lock size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
                      <input
                        type={showPw[key] ? "text" : "password"}
                        value={pwForm[key]}
                        onChange={(e) => setPwForm(p => ({ ...p, [key]: e.target.value }))}
                        placeholder={placeholder}
                        className="w-full bg-white/5 border border-white/10 rounded-xl pl-11 pr-10 py-2.5 text-sm text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/30 transition"
                      />
                      <button type="button" onClick={() => setShowPw(p => ({ ...p, [key]: !p[key] }))}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-300">
                        {showPw[key] ? <EyeOff size={16} /> : <Eye size={16} />}
                      </button>
                    </div>
                  </div>
                ))}
                <button type="submit" disabled={passwordMutation.isPending}
                  className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl px-5 py-2.5 text-sm font-semibold transition disabled:opacity-60">
                  {passwordMutation.isPending ? <Loader2 size={16} className="animate-spin" /> : null}
                  Update Password
                </button>
              </form>
            </div>
          )}

          {/* Subscription Tab */}
          {activeTab === "subscription" && (
            <div>
              <h2 className="text-lg font-bold text-white mb-6">Subscription</h2>
              <div className="bg-indigo-500/10 border border-indigo-500/20 rounded-2xl p-6 mb-6">
                <div className="flex items-center gap-3 mb-4">
                  <Crown size={20} className="text-indigo-400" />
                  <div>
                    <p className="text-white font-semibold">Free Plan</p>
                    <p className="text-slate-400 text-sm">10 links/month • Basic analytics</p>
                  </div>
                </div>
                <Link to="/pricing"
                  className="inline-flex items-center gap-2 bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-500 hover:to-violet-500 text-white rounded-xl px-5 py-2.5 text-sm font-semibold transition">
                  <Crown size={16} /> Upgrade to Pro
                </Link>
              </div>
            </div>
          )}

          {/* Danger Zone */}
          {activeTab === "danger" && (
            <div>
              <h2 className="text-lg font-bold text-white mb-2">Danger Zone</h2>
              <p className="text-slate-400 text-sm mb-6">These actions are irreversible. Please be careful.</p>
              <div className="border border-red-500/20 rounded-2xl p-6 bg-red-500/5">
                <h3 className="text-red-400 font-semibold mb-1">Delete Account</h3>
                <p className="text-slate-400 text-sm mb-4">
                  Permanently delete your account and all your links. This cannot be undone.
                </p>
                <button
                  onClick={() => {
                    if (confirm("Are you SURE you want to delete your account? All your links will be deleted.")) {
                      toast.error("Account deletion coming soon — contact support@shortify.com");
                    }
                  }}
                  className="flex items-center gap-2 bg-red-600/20 hover:bg-red-600/30 text-red-400 border border-red-500/30 rounded-xl px-5 py-2.5 text-sm font-semibold transition"
                >
                  <Trash2 size={16} /> Delete My Account
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </DashboardLayout>
  );
}
