import { useState } from "react";
import { useParams, Link } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { AreaChart, Area, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from "recharts";
import { ArrowLeft, TrendingUp, MousePointerClick, Globe, Smartphone, Loader2, Monitor, Tablet } from "lucide-react";
import { analyticsService } from "../../services/analyticsService";
import DashboardLayout from "../../components/DashboardLayout";
import { formatDate } from "../../lib/dateUtils";

const DEVICE_COLORS = { DESKTOP: "#6366f1", MOBILE: "#a78bfa", TABLET: "#34d399", BOT: "#f59e0b", UNKNOWN: "#64748b" };
const COUNTRY_COLORS = ["#6366f1", "#8b5cf6", "#a78bfa", "#c4b5fd", "#ddd6fe"];

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-slate-800 border border-white/10 rounded-xl px-4 py-3 text-sm shadow-xl">
      <p className="text-slate-400 mb-1">{label}</p>
      <p className="text-white font-bold">{payload[0]?.value?.toLocaleString()} clicks</p>
    </div>
  );
};

export default function AnalyticsPage() {
  const { shortCode } = useParams();
  const [period, setPeriod] = useState("30d");

  const { data: stats, isLoading } = useQuery({
    queryKey: ["analytics", shortCode, period],
    queryFn: () => analyticsService.getUrlStats(shortCode, period).then(r => r.data),
    enabled: !!shortCode,
  });

  const { data: byDay } = useQuery({
    queryKey: ["analytics-by-day", shortCode, period],
    queryFn: () => analyticsService.getClicksByDay(shortCode, period).then(r => r.data),
    enabled: !!shortCode,
  });

  const { data: byDevice } = useQuery({
    queryKey: ["analytics-device", shortCode],
    queryFn: () => analyticsService.getClicksByDevice(shortCode).then(r => r.data),
    enabled: !!shortCode,
  });

  const { data: byCountry } = useQuery({
    queryKey: ["analytics-country", shortCode],
    queryFn: () => analyticsService.getClicksByCountry(shortCode).then(r => r.data),
    enabled: !!shortCode,
  });

  if (isLoading) {
    return (
      <DashboardLayout>
        <div className="flex items-center justify-center py-20">
          <Loader2 size={32} className="animate-spin text-indigo-400" />
        </div>
      </DashboardLayout>
    );
  }

  const PERIODS = ["7d", "30d", "90d", "1y"];

  return (
    <DashboardLayout>
      {/* Back */}
      <Link to="/dashboard" className="inline-flex items-center gap-2 text-slate-400 hover:text-white text-sm mb-6 transition">
        <ArrowLeft size={16} /> Back to Links
      </Link>

      {/* Header */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-white">Analytics</h1>
        {stats && (
          <p className="text-slate-400 text-sm mt-1">
            <span className="text-indigo-400 font-medium">/{shortCode}</span> → {stats.originalUrl}
          </p>
        )}
      </div>

      {/* Period selector */}
      <div className="flex gap-2 mb-8">
        {PERIODS.map((p) => (
          <button
            key={p}
            onClick={() => setPeriod(p)}
            className={`px-4 py-1.5 rounded-xl text-sm font-medium transition
              ${period === p ? "bg-indigo-600 text-white" : "bg-white/5 border border-white/10 text-slate-400 hover:text-white"}`}
          >
            {p}
          </button>
        ))}
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        {[
          { label: "Total Clicks", value: stats?.totalClicks ?? 0, icon: MousePointerClick, color: "indigo" },
          { label: "Unique Visitors", value: stats?.uniqueVisitors ?? 0, icon: Globe, color: "violet" },
          { label: "This Period", value: stats?.clicksInPeriod ?? 0, icon: TrendingUp, color: "emerald" },
          { label: "Avg Daily", value: stats?.avgDailyClicks ?? 0, icon: BarChart2, color: "amber" },
        ].map(({ label, value, icon: Icon, color }) => (
          <div key={label} className="bg-white/5 border border-white/10 rounded-2xl p-5">
            <div className="flex items-center justify-between mb-3">
              <p className="text-slate-400 text-xs">{label}</p>
              <Icon size={16} className={`text-${color}-400`} />
            </div>
            <p className="text-2xl font-bold text-white">{value?.toLocaleString()}</p>
          </div>
        ))}
      </div>

      {/* Clicks over time */}
      <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-6">
        <h3 className="text-white font-semibold mb-6">Clicks Over Time</h3>
        {byDay?.length > 0 ? (
          <ResponsiveContainer width="100%" height={260}>
            <AreaChart data={byDay}>
              <defs>
                <linearGradient id="clickGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#6366f1" stopOpacity={0.3} />
                  <stop offset="95%" stopColor="#6366f1" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#ffffff0a" />
              <XAxis dataKey="date" tick={{ fill: "#64748b", fontSize: 12 }} tickLine={false} axisLine={false} />
              <YAxis tick={{ fill: "#64748b", fontSize: 12 }} tickLine={false} axisLine={false} />
              <Tooltip content={<CustomTooltip />} />
              <Area type="monotone" dataKey="clicks" stroke="#6366f1" strokeWidth={2}
                fill="url(#clickGradient)" dot={false} activeDot={{ r: 5, fill: "#6366f1" }} />
            </AreaChart>
          </ResponsiveContainer>
        ) : (
          <div className="flex items-center justify-center h-40 text-slate-500 text-sm">
            No click data for this period yet
          </div>
        )}
      </div>

      {/* Device + Country row */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Device breakdown */}
        <div className="bg-white/5 border border-white/10 rounded-2xl p-6">
          <h3 className="text-white font-semibold mb-6">Device Types</h3>
          {byDevice?.length > 0 ? (
            <div className="flex items-center gap-6">
              <ResponsiveContainer width="50%" height={160}>
                <PieChart>
                  <Pie data={byDevice} cx="50%" cy="50%" innerRadius={45} outerRadius={70}
                    paddingAngle={3} dataKey="count" nameKey="deviceType">
                    {byDevice.map((entry) => (
                      <Cell key={entry.deviceType} fill={DEVICE_COLORS[entry.deviceType] || "#64748b"} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
              <div className="space-y-2 flex-1">
                {byDevice.map((d) => (
                  <div key={d.deviceType} className="flex items-center justify-between text-sm">
                    <div className="flex items-center gap-2">
                      <div className="w-2.5 h-2.5 rounded-full" style={{ background: DEVICE_COLORS[d.deviceType] }} />
                      <span className="text-slate-400">{d.deviceType}</span>
                    </div>
                    <span className="text-white font-medium">{d.percentage}%</span>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <div className="flex items-center justify-center h-32 text-slate-500 text-sm">No device data yet</div>
          )}
        </div>

        {/* Top countries */}
        <div className="bg-white/5 border border-white/10 rounded-2xl p-6">
          <h3 className="text-white font-semibold mb-6">Top Countries</h3>
          {byCountry?.length > 0 ? (
            <div className="space-y-3">
              {byCountry.slice(0, 5).map((c, i) => (
                <div key={c.country} className="flex items-center gap-3">
                  <span className="text-slate-500 text-xs w-4">{i + 1}</span>
                  <span className="text-slate-300 text-sm flex-1">{c.country || "Unknown"}</span>
                  <div className="flex-1 bg-white/5 rounded-full h-1.5 max-w-32">
                    <div
                      className="h-1.5 rounded-full"
                      style={{ width: `${c.percentage}%`, background: COUNTRY_COLORS[i] }}
                    />
                  </div>
                  <span className="text-white text-sm font-medium w-10 text-right">
                    {c.percentage}%
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <div className="flex items-center justify-center h-32 text-slate-500 text-sm">No country data yet</div>
          )}
        </div>
      </div>
    </DashboardLayout>
  );
}
