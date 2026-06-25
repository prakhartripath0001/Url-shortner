import { useState } from "react";
import { Link } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Link2, Trash2, Copy, QrCode, BarChart2, Plus, ExternalLink, Search, Loader2, Globe, Lock } from "lucide-react";
import toast from "react-hot-toast";
import { urlService } from "../services/urlService";
import { useAuthStore } from "../store/authStore";
import DashboardLayout from "../components/DashboardLayout";
import CreateUrlModal from "../components/CreateUrlModal";
import QrCodeModal from "../components/QrCodeModal";
import { formatDistanceToNow } from "../lib/dateUtils";

export default function DashboardPage() {
  const user = useAuthStore((s) => s.user);
  const queryClient = useQueryClient();
  const [search, setSearch] = useState("");
  const [showCreate, setShowCreate] = useState(false);
  const [page, setPage] = useState(0);
  const [qrModalUrl, setQrModalUrl] = useState(null);

  const { data, isLoading } = useQuery({
    queryKey: ["urls", page],
    queryFn: () => urlService.listUrls(page, 12).then(r => r.data),
    keepPreviousData: true,
  });

  const deleteMutation = useMutation({
    mutationFn: (shortCode) => urlService.deleteUrl(shortCode),
    onSuccess: () => {
      queryClient.invalidateQueries(["urls"]);
      toast.success("Link deleted");
    },
    onError: () => toast.error("Failed to delete link"),
  });

  const copyToClipboard = (url) => {
    navigator.clipboard.writeText(url);
    toast.success("Copied to clipboard!");
  };

  const urls = data?.content || [];
  const filtered = urls.filter(u =>
    u.originalUrl?.toLowerCase().includes(search.toLowerCase()) ||
    u.shortCode?.toLowerCase().includes(search.toLowerCase()) ||
    u.title?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <DashboardLayout>
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
        <div>
          <h1 className="text-2xl font-bold text-white">My Links</h1>
          <p className="text-slate-400 text-sm mt-1">
            {data?.totalElements ?? 0} links created
          </p>
        </div>
        <button
          id="create-url-btn"
          onClick={() => setShowCreate(true)}
          className="inline-flex items-center gap-2 bg-gradient-to-r from-indigo-600 to-violet-600
                     hover:from-indigo-500 hover:to-violet-500 text-white rounded-xl px-5 py-2.5
                     text-sm font-semibold transition-all shadow-lg shadow-indigo-500/25"
        >
          <Plus size={18} /> New Link
        </button>
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        {[
          { label: "Total Links", value: data?.totalElements ?? "—", icon: Link2, color: "indigo" },
          { label: "Total Clicks", value: urls.reduce((s, u) => s + (u.visitCount || 0), 0).toLocaleString(), icon: BarChart2, color: "violet" },
          { label: "Active Links", value: urls.filter(u => !u.expiresAt || new Date(u.expiresAt) > new Date()).length, icon: Globe, color: "emerald" },
        ].map(({ label, value, icon: Icon, color }) => (
          <div key={label} className="bg-white/5 border border-white/10 rounded-2xl p-5 backdrop-blur-sm">
            <div className="flex items-center justify-between mb-3">
              <p className="text-slate-400 text-sm">{label}</p>
              <div className={`w-9 h-9 rounded-xl bg-${color}-500/20 flex items-center justify-center`}>
                <Icon size={18} className={`text-${color}-400`} />
              </div>
            </div>
            <p className="text-2xl font-bold text-white">{value}</p>
          </div>
        ))}
      </div>

      {/* Search */}
      <div className="relative mb-6">
        <Search size={17} className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" />
        <input
          type="text"
          placeholder="Search links..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full bg-white/5 border border-white/10 rounded-xl pl-11 pr-4 py-3 text-sm text-slate-300 placeholder-slate-500
                     focus:outline-none focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500/50 transition"
        />
      </div>

      {/* URL Table */}
      {isLoading ? (
        <div className="flex items-center justify-center py-20">
          <Loader2 size={32} className="animate-spin text-indigo-400" />
        </div>
      ) : filtered.length === 0 ? (
        <div className="text-center py-20 bg-white/5 border border-white/10 rounded-2xl">
          <Link2 size={48} className="text-slate-600 mx-auto mb-4" />
          <p className="text-slate-400 font-medium mb-2">No links yet</p>
          <p className="text-slate-500 text-sm mb-6">Create your first short link to get started</p>
          <button onClick={() => setShowCreate(true)}
            className="inline-flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl px-5 py-2.5 text-sm font-semibold transition">
            <Plus size={16} /> Create Link
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          {filtered.map((url) => (
            <div key={url.id}
              className="group bg-white/5 border border-white/10 hover:border-indigo-500/30 rounded-2xl p-5 
                         backdrop-blur-sm transition-all duration-200 hover:bg-white/[0.07]"
            >
              <div className="flex items-start gap-4">
                {/* Favicon */}
                <div className="w-10 h-10 rounded-xl bg-indigo-500/20 flex items-center justify-center flex-shrink-0 mt-0.5">
                  <img
                    src={`https://www.google.com/s2/favicons?sz=32&domain_url=${encodeURIComponent(url.originalUrl)}`}
                    alt=""
                    className="w-5 h-5 rounded"
                    onError={(e) => { e.target.style.display = "none"; }}
                  />
                </div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 flex-wrap">
                    <span className="text-white font-semibold text-sm">{url.title || url.shortCode}</span>
                    {url.isPrivate && (
                      <span className="inline-flex items-center gap-1 text-xs bg-amber-500/20 text-amber-400 border border-amber-500/20 rounded-full px-2 py-0.5">
                        <Lock size={10} /> Private
                      </span>
                    )}
                  </div>

                  <div className="flex items-center gap-2 mt-1">
                    <a
                      href={url.shortUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-indigo-400 hover:text-indigo-300 text-xs font-medium transition"
                    >
                      {url.shortUrl}
                    </a>
                  </div>

                  <p className="text-slate-500 text-xs mt-0.5 truncate max-w-md">{url.originalUrl}</p>

                  <div className="flex items-center gap-4 mt-3">
                    <span className="text-xs text-slate-500 flex items-center gap-1">
                      <BarChart2 size={12} />
                      {(url.visitCount || 0).toLocaleString()} clicks
                    </span>
                    <span className="text-xs text-slate-600">
                      {formatDistanceToNow(url.createdAt)}
                    </span>
                    {url.expiresAt && (
                      <span className="text-xs text-amber-500">
                        Expires {formatDistanceToNow(url.expiresAt)}
                      </span>
                    )}
                  </div>
                </div>

                {/* Actions */}
                <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button
                    onClick={() => copyToClipboard(url.shortUrl)}
                    className="p-2 rounded-lg text-slate-400 hover:text-white hover:bg-white/10 transition"
                    title="Copy link"
                  >
                    <Copy size={16} />
                  </button>
                  <a
                    href={url.shortUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="p-2 rounded-lg text-slate-400 hover:text-white hover:bg-white/10 transition"
                    title="Open link"
                  >
                    <ExternalLink size={16} />
                  </a>
                  <Link
                    to={`/analytics/${url.shortCode}`}
                    className="p-2 rounded-lg text-slate-400 hover:text-indigo-400 hover:bg-indigo-500/10 transition"
                    title="View analytics"
                  >
                    <BarChart2 size={16} />
                  </Link>
                  <button
                    onClick={() => setQrModalUrl({ shortCode: url.shortCode, shortUrl: url.shortUrl })}
                    className="p-2 rounded-lg text-slate-400 hover:text-emerald-400 hover:bg-emerald-500/10 transition"
                    title="QR Code"
                  >
                    <QrCode size={16} />
                  </button>
                  <button
                    onClick={() => {
                      if (confirm("Delete this link?")) deleteMutation.mutate(url.shortCode);
                    }}
                    className="p-2 rounded-lg text-slate-400 hover:text-red-400 hover:bg-red-500/10 transition"
                    title="Delete link"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination */}
      {data && data.totalPages > 1 && (
        <div className="flex items-center justify-center gap-3 mt-8">
          <button
            onClick={() => setPage(p => Math.max(0, p - 1))}
            disabled={page === 0}
            className="px-4 py-2 rounded-xl bg-white/5 border border-white/10 text-slate-300 text-sm
                       hover:bg-white/10 disabled:opacity-40 disabled:cursor-not-allowed transition"
          >
            Previous
          </button>
          <span className="text-slate-400 text-sm">Page {page + 1} of {data.totalPages}</span>
          <button
            onClick={() => setPage(p => Math.min(data.totalPages - 1, p + 1))}
            disabled={page === data.totalPages - 1}
            className="px-4 py-2 rounded-xl bg-white/5 border border-white/10 text-slate-300 text-sm
                       hover:bg-white/10 disabled:opacity-40 disabled:cursor-not-allowed transition"
          >
            Next
          </button>
        </div>
      )}

      {/* Create URL Modal */}
      <CreateUrlModal
        open={showCreate}
        onClose={() => setShowCreate(false)}
        onSuccess={() => {
          queryClient.invalidateQueries(["urls"]);
          setShowCreate(false);
        }}
      />

      {/* QR Code Modal */}
      <QrCodeModal
        open={!!qrModalUrl}
        onClose={() => setQrModalUrl(null)}
        shortCode={qrModalUrl?.shortCode}
        shortUrl={qrModalUrl?.shortUrl}
      />
    </DashboardLayout>
  );
}
