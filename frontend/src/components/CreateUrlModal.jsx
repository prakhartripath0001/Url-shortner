// src/components/CreateUrlModal.jsx
import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { X, Link2, Hash, Type, Calendar, Lock, Loader2, CheckCircle2, Copy, QrCode } from "lucide-react";
import toast from "react-hot-toast";
import { urlService } from "../services/urlService";
import { QRCodeSVG } from "qrcode.react";

export default function CreateUrlModal({ open, onClose, onSuccess }) {
  const [form, setForm] = useState({
    originalUrl: "", customAlias: "", title: "",
    expiresAt: "", isPrivate: false,
  });
  const [created, setCreated] = useState(null);
  const [showQr, setShowQr] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const e = {};
    if (!form.originalUrl) e.originalUrl = "URL is required";
    else if (!/^https?:\/\/.+/.test(form.originalUrl)) e.originalUrl = "Must start with http:// or https://";
    if (form.customAlias && !/^[a-zA-Z0-9\-_]{3,50}$/.test(form.customAlias)) {
      e.customAlias = "3-50 chars, letters/numbers/hyphens only";
    }
    return e;
  };

  const mutation = useMutation({
    mutationFn: (data) => urlService.createUrl(data).then(r => r.data),
    onSuccess: (data) => {
      setCreated(data);
    },
    onError: (err) => {
      const msg = err.response?.data?.detail || "Failed to create link";
      toast.error(msg);
    },
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    mutation.mutate({
      originalUrl: form.originalUrl,
      customAlias: form.customAlias || undefined,
      title: form.title || undefined,
      expiresAt: form.expiresAt ? new Date(form.expiresAt).toISOString() : undefined,
      isPrivate: form.isPrivate,
    });
  };

  const handleDone = () => {
    onSuccess?.();
    setCreated(null);
    setForm({ originalUrl: "", customAlias: "", title: "", expiresAt: "", isPrivate: false });
    setErrors({});
    onClose();
  };

  const copyLink = () => {
    navigator.clipboard.writeText(created.shortUrl);
    toast.success("Copied!");
  };

  const field = (name) => ({
    value: form[name],
    onChange: (e) => {
      const val = e.target.type === "checkbox" ? e.target.checked : e.target.value;
      setForm(p => ({ ...p, [name]: val }));
      setErrors(p => ({ ...p, [name]: null }));
    },
  });

  const inputCls = (name) =>
    `w-full bg-white/5 border rounded-xl px-4 py-2.5 text-sm text-white placeholder-slate-500
     focus:outline-none focus:ring-2 transition-all
     ${errors[name] ? "border-red-500/50 focus:ring-red-500/20" : "border-white/10 focus:border-indigo-500/50 focus:ring-indigo-500/20"}`;

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-black/70 backdrop-blur-sm" onClick={onClose} />
      <div className="relative w-full max-w-lg bg-slate-900 border border-white/10 rounded-2xl shadow-2xl overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-white/5">
          <h2 className="text-lg font-bold text-white">
            {created ? "Link Created! 🎉" : "Create Short Link"}
          </h2>
          <button onClick={onClose} className="p-2 rounded-xl text-slate-400 hover:text-white hover:bg-white/10 transition">
            <X size={18} />
          </button>
        </div>

        <div className="p-6">
          {/* Success State */}
          {created ? (
            <div className="space-y-5">
              <div className="flex items-center gap-3 p-4 bg-green-500/10 border border-green-500/20 rounded-xl">
                <CheckCircle2 size={20} className="text-green-400 flex-shrink-0" />
                <div className="flex-1 min-w-0">
                  <p className="text-green-400 font-semibold text-sm">Your link is ready!</p>
                  <p className="text-slate-400 text-xs truncate mt-0.5">{created.originalUrl}</p>
                </div>
              </div>

              {/* Short URL display */}
              <div className="flex items-center gap-2 bg-white/5 border border-white/10 rounded-xl px-4 py-3">
                <Link2 size={16} className="text-indigo-400 flex-shrink-0" />
                <span className="flex-1 text-indigo-300 font-medium text-sm truncate">{created.shortUrl}</span>
                <button onClick={copyLink} className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/10 transition">
                  <Copy size={16} />
                </button>
              </div>

              {/* QR Toggle */}
              <button
                onClick={() => setShowQr(v => !v)}
                className="w-full flex items-center justify-center gap-2 py-2 text-sm text-slate-400 hover:text-indigo-400 transition"
              >
                <QrCode size={16} /> {showQr ? "Hide" : "Show"} QR Code
              </button>

              {showQr && (
                <div className="flex justify-center p-4 bg-white rounded-xl">
                  <QRCodeSVG value={created.shortUrl} size={180} level="M" />
                </div>
              )}

              <div className="flex gap-3">
                <button onClick={handleDone}
                  className="flex-1 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold transition">
                  Done
                </button>
                <button onClick={() => setCreated(null)}
                  className="flex-1 py-2.5 rounded-xl bg-white/5 border border-white/10 text-slate-300 hover:text-white text-sm font-semibold transition">
                  Create Another
                </button>
              </div>
            </div>
          ) : (
            /* Create Form */
            <form onSubmit={handleSubmit} className="space-y-4">
              {/* URL */}
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">
                  <span className="flex items-center gap-1.5"><Link2 size={13} /> Destination URL *</span>
                </label>
                <input id="create-url-input" type="url" placeholder="https://your-long-url.com/page" className={inputCls("originalUrl")} {...field("originalUrl")} />
                {errors.originalUrl && <p className="text-xs text-red-400 mt-1">{errors.originalUrl}</p>}
              </div>

              {/* Custom Alias */}
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">
                  <span className="flex items-center gap-1.5"><Hash size={13} /> Custom Alias (optional)</span>
                </label>
                <div className="flex gap-2">
                  <span className="bg-white/5 border border-white/10 rounded-xl px-3 py-2.5 text-slate-500 text-sm whitespace-nowrap">
                    short.ly/
                  </span>
                  <input type="text" placeholder="my-brand" className={`${inputCls("customAlias")} flex-1`} {...field("customAlias")} />
                </div>
                {errors.customAlias && <p className="text-xs text-red-400 mt-1">{errors.customAlias}</p>}
              </div>

              {/* Title & Expiry in a row */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1.5">
                    <span className="flex items-center gap-1.5"><Type size={13} /> Title</span>
                  </label>
                  <input type="text" placeholder="My Campaign" className={inputCls("title")} {...field("title")} />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1.5">
                    <span className="flex items-center gap-1.5"><Calendar size={13} /> Expires At</span>
                  </label>
                  <input type="datetime-local" className={inputCls("expiresAt")} {...field("expiresAt")} />
                </div>
              </div>

              {/* Private toggle */}
              <label className="flex items-center gap-3 cursor-pointer p-3 rounded-xl border border-white/10 hover:border-white/20 transition">
                <input type="checkbox" checked={form.isPrivate} onChange={field("isPrivate").onChange} className="sr-only" />
                <div className={`w-9 h-5 rounded-full transition-colors ${form.isPrivate ? "bg-indigo-500" : "bg-slate-700"}`}>
                  <div className={`w-3.5 h-3.5 bg-white rounded-full m-0.5 transition-transform ${form.isPrivate ? "translate-x-4" : ""}`} />
                </div>
                <span className="flex items-center gap-1.5 text-sm text-slate-300">
                  <Lock size={14} /> Private link
                </span>
                <span className="ml-auto text-xs text-slate-500">Requires login to access</span>
              </label>

              <button
                id="create-url-submit"
                type="submit"
                disabled={mutation.isPending}
                className="w-full flex items-center justify-center gap-2 rounded-xl
                           bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-500 hover:to-violet-500
                           py-3 text-sm font-semibold text-white transition-all
                           disabled:opacity-60 disabled:cursor-not-allowed shadow-lg shadow-indigo-500/25"
              >
                {mutation.isPending ? <Loader2 size={18} className="animate-spin" /> : "Shorten URL"}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
