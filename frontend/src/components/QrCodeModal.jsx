import { useState, useEffect } from "react";
import { X, Copy, Download, Loader2 } from "lucide-react";
import toast from "react-hot-toast";
import { urlService } from "../services/urlService";

export default function QrCodeModal({ open, onClose, shortCode, shortUrl }) {
  const [qrBlobUrl, setQrBlobUrl] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!open || !shortCode) return;

    setLoading(true);
    setQrBlobUrl(null);

    urlService
      .getQrCode(shortCode, 300)
      .then((res) => {
        const url = URL.createObjectURL(res.data);
        setQrBlobUrl(url);
      })
      .catch((err) => {
        console.error("Failed to load QR code", err);
        toast.error("Failed to load QR code from server.");
      })
      .finally(() => {
        setLoading(false);
      });

    // Cleanup blob url on unmount or when details change
    return () => {
      if (qrBlobUrl) {
        URL.revokeObjectURL(qrBlobUrl);
      }
    };
  }, [open, shortCode]);

  if (!open) return null;

  const copyUrl = () => {
    navigator.clipboard.writeText(shortUrl);
    toast.success("Copied to clipboard!");
  };

  const downloadQr = () => {
    if (!qrBlobUrl) return;
    const link = document.createElement("a");
    link.href = qrBlobUrl;
    link.download = `qr-${shortCode}.png`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    toast.success("QR Code downloaded!");
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop */}
      <div className="absolute inset-0 bg-black/70 backdrop-blur-sm" onClick={onClose} />
      
      {/* Container */}
      <div className="relative w-full max-w-sm bg-slate-900 border border-white/10 rounded-2xl shadow-2xl overflow-hidden p-6">
        
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <h3 className="text-lg font-bold text-white">QR Code</h3>
            <p className="text-slate-400 text-xs mt-0.5">Scan to navigate to your link</p>
          </div>
          <button
            onClick={onClose}
            className="p-2 rounded-xl text-slate-400 hover:text-white hover:bg-white/10 transition"
          >
            <X size={18} />
          </button>
        </div>

        {/* QR Code Container */}
        <div className="flex flex-col items-center justify-center bg-white/5 border border-white/5 rounded-2xl p-6 mb-6 min-h-[220px]">
          {loading ? (
            <div className="flex flex-col items-center gap-2 text-slate-400 text-sm">
              <Loader2 className="animate-spin text-indigo-400" size={32} />
              Generating QR code...
            </div>
          ) : qrBlobUrl ? (
            <img
              src={qrBlobUrl}
              alt={`QR Code for ${shortCode}`}
              className="w-48 h-48 rounded-lg border-4 border-white shadow-lg bg-white"
            />
          ) : (
            <div className="text-slate-500 text-sm text-center">
              Failed to load QR code.
            </div>
          )}
        </div>

        {/* Info */}
        <div className="bg-white/5 border border-white/10 rounded-xl px-4 py-3 mb-6 flex items-center gap-2">
          <span className="flex-1 text-xs text-indigo-300 font-medium truncate font-mono">
            {shortUrl}
          </span>
          <button
            onClick={copyUrl}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/10 transition"
            title="Copy URL"
          >
            <Copy size={14} />
          </button>
        </div>

        {/* Actions */}
        <button
          onClick={downloadQr}
          disabled={!qrBlobUrl || loading}
          className="w-full flex items-center justify-center gap-2 rounded-xl
                     bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-500 hover:to-violet-500
                     py-3 text-sm font-semibold text-white transition-all shadow-lg shadow-indigo-500/25
                     disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <Download size={16} /> Download PNG
        </button>

      </div>
    </div>
  );
}
