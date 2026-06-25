import { useState } from "react";
import { Link } from "react-router-dom";
import { Zap, Link2, BarChart2, Shield, QrCode, Clock, ArrowRight, Star, Globe, Copy, Check } from "lucide-react";

const FEATURES = [
  { icon: Link2, title: "Instant Shortening", desc: "Transform any URL into a clean, shareable link in milliseconds." },
  { icon: BarChart2, title: "Deep Analytics", desc: "Track clicks, devices, countries, and referrers in real time." },
  { icon: QrCode, title: "QR Code Generation", desc: "Generate QR codes for any link, instantly downloadable." },
  { icon: Shield, title: "Private Links", desc: "Create password-protected or login-required links." },
  { icon: Clock, title: "Link Expiry", desc: "Set expiration dates — links auto-deactivate after your event." },
  { icon: Globe, title: "Custom Aliases", desc: "Brand your links with memorable custom slugs." },
];

const STATS = [
  { value: "10M+", label: "Links Created" },
  { value: "500M+", label: "Clicks Tracked" },
  { value: "99.99%", label: "Uptime SLA" },
  { value: "< 50ms", label: "Redirect Speed" },
];

export default function HomePage() {
  const [inputUrl, setInputUrl] = useState("");
  const [copied, setCopied] = useState(false);

  const handleDemo = (e) => {
    e.preventDefault();
    // Demo: just copy a fake short URL
    navigator.clipboard.writeText("https://short.ly/demo-abc");
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-indigo-950/50 to-slate-950 text-white overflow-x-hidden">
      {/* Navbar */}
      <nav className="fixed top-0 inset-x-0 z-50 h-16 flex items-center justify-between px-6 lg:px-12 
                      bg-slate-950/80 backdrop-blur-xl border-b border-white/5">
        <Link to="/" className="flex items-center gap-2 font-bold text-lg">
          <div className="w-8 h-8 bg-gradient-to-br from-indigo-500 to-violet-600 rounded-lg flex items-center justify-center">
            <Zap size={18} />
          </div>
          Shortify
        </Link>
        <div className="hidden md:flex items-center gap-6 text-sm text-slate-400">
          <a href="#features" className="hover:text-white transition">Features</a>
          <Link to="/pricing" className="hover:text-white transition">Pricing</Link>
          <a href="#" className="hover:text-white transition">Docs</a>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/login" className="text-sm text-slate-400 hover:text-white transition px-3 py-1.5">Sign In</Link>
          <Link to="/signup"
            className="text-sm bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl px-4 py-2 font-semibold transition shadow-lg shadow-indigo-500/20">
            Get Started
          </Link>
        </div>
      </nav>

      {/* Hero */}
      <section className="relative pt-40 pb-28 px-6 text-center">
        {/* Background glow */}
        <div className="absolute inset-0 pointer-events-none">
          <div className="absolute top-20 left-1/2 -translate-x-1/2 w-[800px] h-[500px] bg-indigo-500/10 rounded-full blur-[100px]" />
        </div>

        <div className="relative max-w-4xl mx-auto">
          <div className="inline-flex items-center gap-2 bg-indigo-500/10 border border-indigo-500/20 rounded-full px-4 py-1.5 text-indigo-400 text-sm font-medium mb-6">
            <Star size={13} fill="currentColor" /> Trusted by 50,000+ creators worldwide
          </div>

          <h1 className="text-5xl lg:text-7xl font-extrabold tracking-tight mb-6 leading-tight">
            Shorten links.{" "}
            <span className="bg-gradient-to-r from-indigo-400 via-violet-400 to-purple-400 bg-clip-text text-transparent">
              Grow faster.
            </span>
          </h1>

          <p className="text-lg lg:text-xl text-slate-400 max-w-2xl mx-auto mb-10 leading-relaxed">
            The smartest URL shortener with real-time analytics, custom aliases,
            QR codes, and enterprise-grade reliability — all in one platform.
          </p>

          {/* Quick shorten form */}
          <form onSubmit={handleDemo}
            className="flex flex-col sm:flex-row gap-3 max-w-xl mx-auto mb-12">
            <div className="relative flex-1">
              <Link2 size={17} className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" />
              <input
                id="hero-url-input"
                type="url"
                value={inputUrl}
                onChange={(e) => setInputUrl(e.target.value)}
                placeholder="https://your-very-long-url.com/article/..."
                className="w-full bg-white/5 border border-white/10 rounded-xl pl-11 pr-4 py-4 text-sm text-white placeholder-slate-500
                           focus:outline-none focus:ring-2 focus:ring-indigo-500/40 focus:border-indigo-500/40 transition"
              />
            </div>
            <button type="submit" id="hero-shorten-btn"
              className="flex items-center justify-center gap-2 bg-gradient-to-r from-indigo-600 to-violet-600
                         hover:from-indigo-500 hover:to-violet-500 text-white rounded-xl px-6 py-4 font-semibold text-sm
                         transition-all shadow-lg shadow-indigo-500/25 whitespace-nowrap">
              {copied ? <><Check size={16} /> Copied!</> : <><Zap size={16} /> Shorten Free</>}
            </button>
          </form>

          <p className="text-slate-500 text-xs">No credit card required • Free forever plan available</p>
        </div>
      </section>

      {/* Stats */}
      <section className="py-16 px-6 border-y border-white/5 bg-white/[0.02]">
        <div className="max-w-5xl mx-auto grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          {STATS.map(({ value, label }) => (
            <div key={label}>
              <p className="text-3xl lg:text-4xl font-extrabold bg-gradient-to-r from-indigo-400 to-violet-400 bg-clip-text text-transparent">{value}</p>
              <p className="text-slate-500 text-sm mt-1">{label}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Features */}
      <section id="features" className="py-24 px-6">
        <div className="max-w-6xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl lg:text-4xl font-bold mb-4">
              Everything you need to manage links
            </h2>
            <p className="text-slate-400 max-w-xl mx-auto">
              From one-click shortening to enterprise analytics — Shortify handles it all.
            </p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {FEATURES.map(({ icon: Icon, title, desc }) => (
              <div key={title}
                className="group bg-white/5 border border-white/10 hover:border-indigo-500/30 rounded-2xl p-7
                           hover:bg-white/[0.07] transition-all duration-200">
                <div className="w-12 h-12 bg-indigo-500/20 group-hover:bg-indigo-500/30 rounded-xl flex items-center justify-center mb-5 transition">
                  <Icon size={22} className="text-indigo-400" />
                </div>
                <h3 className="text-white font-semibold mb-2">{title}</h3>
                <p className="text-slate-400 text-sm leading-relaxed">{desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-24 px-6 text-center">
        <div className="max-w-2xl mx-auto">
          <div className="bg-gradient-to-r from-indigo-600/20 to-violet-600/20 border border-indigo-500/20 rounded-3xl p-12">
            <h2 className="text-3xl lg:text-4xl font-bold mb-4">
              Ready to grow faster?
            </h2>
            <p className="text-slate-400 mb-8">
              Join 50,000+ creators using Shortify to track and grow their audience.
            </p>
            <Link to="/signup"
              className="inline-flex items-center gap-2 bg-gradient-to-r from-indigo-600 to-violet-600
                         hover:from-indigo-500 hover:to-violet-500 text-white rounded-xl px-8 py-4 font-semibold
                         transition-all shadow-xl shadow-indigo-500/30">
              Start for free <ArrowRight size={18} />
            </Link>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-white/5 py-8 px-6 text-center text-slate-600 text-sm">
        <p>© 2025 Shortify. Built with ❤️ for developers.</p>
      </footer>
    </div>
  );
}
