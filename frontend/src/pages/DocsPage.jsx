import { useState } from "react";
import { Link } from "react-router-dom";
import { Zap, BookOpen, Key, Link2, BarChart2, ShieldAlert, ArrowLeft, Terminal, Copy, Check } from "lucide-react";
import toast from "react-hot-toast";

const SECTIONS = [
  {
    id: "getting-started",
    title: "Getting Started",
    icon: BookOpen,
    description: "Welcome to the Shortify developer documentation. Learn how to authenticate and interface with our microservices.",
  },
  {
    id: "auth-service",
    title: "Auth Service",
    icon: Key,
    description: "Manage users, sessions, and secure access tokens on port 8082.",
    endpoints: [
      {
        method: "POST",
        path: "/api/v1/auth/signup",
        desc: "Register a new developer account.",
        body: JSON.stringify({ username: "john_doe", email: "john@example.com", password: "Password123!" }, null, 2),
        response: JSON.stringify({ message: "User registered successfully" }, null, 2),
      },
      {
        method: "POST",
        path: "/api/v1/auth/login",
        desc: "Authenticate to receive JWT access and refresh tokens.",
        body: JSON.stringify({ email: "john@example.com", password: "Password123!" }, null, 2),
        response: JSON.stringify({ accessToken: "eyJhbGciOi...", refreshToken: "def123...", user: { id: 1, username: "john_doe", email: "john@example.com" } }, null, 2),
      },
    ],
  },
  {
    id: "url-service",
    title: "URL Service",
    icon: Link2,
    description: "Shorten destinations, manage links, and generate QR codes on port 8083.",
    endpoints: [
      {
        method: "POST",
        path: "/api/v1/urls",
        desc: "Create a shortened URL with options.",
        headers: { Authorization: "Bearer <token>" },
        body: JSON.stringify({ originalUrl: "https://google.com", customAlias: "google-home", title: "Google Main Page", expiresAt: "2026-12-31T23:59:59Z", isPrivate: false }, null, 2),
        response: JSON.stringify({ id: 101, shortCode: "google-home", originalUrl: "https://google.com", shortUrl: "http://localhost:8083/google-home", title: "Google Main Page", createdAt: "2026-06-25T10:00:00Z", expiresAt: "2026-12-31T23:59:59Z", isPrivate: false, visitCount: 0 }, null, 2),
      },
      {
        method: "GET",
        path: "/api/v1/urls/{shortCode}/qr",
        desc: "Get a high-quality PNG QR code for any shortened URL.",
        response: "[Binary PNG Image Data]",
      },
    ],
  },
  {
    id: "analytics-service",
    title: "Analytics Service",
    icon: BarChart2,
    description: "Access real-time aggregated metrics for shortened URLs on port 8084.",
    endpoints: [
      {
        method: "GET",
        path: "/api/v1/analytics/{shortCode}",
        desc: "Retrieve overview stats (total clicks, unique visitors, current period clicks).",
        headers: { Authorization: "Bearer <token>" },
        response: JSON.stringify({ shortCode: "google-home", originalUrl: "https://google.com", totalClicks: 1240, uniqueVisitors: 890, clicksInPeriod: 320, avgDailyClicks: 41.3 }, null, 2),
      },
      {
        method: "GET",
        path: "/api/v1/analytics/{shortCode}/device",
        desc: "Fetch breakdown of clicks grouped by device types.",
        headers: { Authorization: "Bearer <token>" },
        response: JSON.stringify([
          { deviceType: "DESKTOP", count: 820, percentage: 66.1 },
          { deviceType: "MOBILE", count: 370, percentage: 29.8 },
          { deviceType: "TABLET", count: 50, percentage: 4.1 }
        ], null, 2),
      },
    ],
  },
];

export default function DocsPage() {
  const [activeSection, setActiveSection] = useState("getting-started");
  const [copiedId, setCopiedId] = useState(null);

  const copyToClipboard = (text, id) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    toast.success("Code snippet copied!");
    setTimeout(() => setCopiedId(null), 2000);
  };

  const activeData = SECTIONS.find((s) => s.id === activeSection);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      {/* Header */}
      <header className="h-16 flex items-center justify-between px-6 lg:px-12 border-b border-white/5 bg-slate-950/80 backdrop-blur-xl sticky top-0 z-40">
        <Link to="/" className="flex items-center gap-2 font-bold text-lg text-white">
          <ArrowLeft size={16} className="text-slate-400 hover:text-white transition" />
          <div className="w-8 h-8 bg-gradient-to-br from-indigo-500 to-violet-600 rounded-lg flex items-center justify-center">
            <Zap size={18} />
          </div>
          Shortify Docs
        </Link>
        <span className="text-xs bg-indigo-500/20 text-indigo-400 border border-indigo-500/20 rounded-full px-3 py-1 font-semibold">
          API v1.0
        </span>
      </header>

      {/* Docs Body */}
      <div className="flex-1 flex flex-col lg:flex-row max-w-7xl w-full mx-auto p-4 lg:p-8 gap-8">
        {/* Sidebar Nav */}
        <aside className="lg:w-64 flex-shrink-0 flex flex-row lg:flex-col overflow-x-auto lg:overflow-visible gap-2 pb-4 lg:pb-0 border-b lg:border-b-0 lg:border-r border-white/5">
          {SECTIONS.map((section) => {
            const Icon = section.icon;
            const active = section.id === activeSection;
            return (
              <button
                key={section.id}
                onClick={() => setActiveSection(section.id)}
                className={`flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm font-medium text-left transition-all whitespace-nowrap lg:whitespace-normal
                  ${active
                    ? "bg-indigo-500/25 text-indigo-400 border border-indigo-500/20"
                    : "text-slate-400 hover:text-white hover:bg-white/5"
                  }`}
              >
                <Icon size={16} />
                {section.title}
              </button>
            );
          })}
        </aside>

        {/* Content Panel */}
        <main className="flex-1 bg-white/5 border border-white/10 rounded-2xl p-6 lg:p-8">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 rounded-xl bg-indigo-500/10 flex items-center justify-center text-indigo-400 border border-indigo-500/20">
              {activeData && <activeData.icon size={20} />}
            </div>
            <h1 className="text-2xl font-bold text-white">{activeData?.title}</h1>
          </div>
          <p className="text-slate-400 text-sm leading-relaxed mb-8">{activeData?.description}</p>

          {/* Render Endpoints if applicable */}
          {activeData?.endpoints ? (
            <div className="space-y-10">
              {activeData.endpoints.map((endpoint, index) => (
                <div key={index} className="border-t border-white/5 pt-8 first:border-0 first:pt-0">
                  <div className="flex items-center gap-2 mb-3">
                    <span className={`px-2.5 py-1 rounded-lg text-xs font-bold tracking-wide
                      ${endpoint.method === "POST" ? "bg-emerald-500/25 text-emerald-400" : "bg-sky-500/25 text-sky-400"}`}>
                      {endpoint.method}
                    </span>
                    <span className="font-mono text-sm text-indigo-300 font-semibold">{endpoint.path}</span>
                  </div>
                  <p className="text-slate-400 text-sm mb-4">{endpoint.desc}</p>

                  {/* Headers */}
                  {endpoint.headers && (
                    <div className="mb-4">
                      <h4 className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Request Headers</h4>
                      <pre className="bg-slate-900 border border-white/5 rounded-xl p-3 text-xs text-indigo-200 font-mono">
                        {JSON.stringify(endpoint.headers, null, 2)}
                      </pre>
                    </div>
                  )}

                  {/* Body / Payload */}
                  {endpoint.body && (
                    <div className="mb-4">
                      <div className="flex justify-between items-center mb-2">
                        <h4 className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Request Body</h4>
                        <button
                          onClick={() => copyToClipboard(endpoint.body, `req-${index}`)}
                          className="text-slate-500 hover:text-slate-300 p-1 rounded transition"
                          title="Copy Request Body"
                        >
                          {copiedId === `req-${index}` ? <Check size={14} className="text-green-400" /> : <Copy size={14} />}
                        </button>
                      </div>
                      <pre className="bg-slate-950 border border-white/5 rounded-xl p-4 text-xs font-mono text-slate-300 overflow-x-auto">
                        {endpoint.body}
                      </pre>
                    </div>
                  )}

                  {/* Response */}
                  {endpoint.response && (
                    <div>
                      <div className="flex justify-between items-center mb-2">
                        <h4 className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Example Response</h4>
                        <button
                          onClick={() => copyToClipboard(endpoint.response, `res-${index}`)}
                          className="text-slate-500 hover:text-slate-300 p-1 rounded transition"
                          title="Copy Response"
                        >
                          {copiedId === `res-${index}` ? <Check size={14} className="text-green-400" /> : <Copy size={14} />}
                        </button>
                      </div>
                      <pre className="bg-slate-950 border border-white/5 rounded-xl p-4 text-xs font-mono text-indigo-200 overflow-x-auto">
                        {endpoint.response}
                      </pre>
                    </div>
                  )}
                </div>
              ))}
            </div>
          ) : (
            /* Getting Started Content */
            <div className="space-y-6 text-sm text-slate-400">
              <div className="bg-indigo-500/10 border border-indigo-500/20 rounded-2xl p-5">
                <h3 className="text-white font-bold mb-2 flex items-center gap-2">
                  <Terminal size={16} className="text-indigo-400" /> API Base Endpoints
                </h3>
                <p className="mb-4">Shortify services are distributed across microservices. Use the following ports on local environments:</p>
                <ul className="space-y-2 font-mono text-xs text-indigo-300">
                  <li>🚀 Auth Service: <span className="text-slate-300">http://localhost:8082</span></li>
                  <li>🔗 URL Service: <span className="text-slate-300">http://localhost:8083</span></li>
                  <li>📊 Analytics Service: <span className="text-slate-300">http://localhost:8084</span></li>
                  <li>💳 Payment Service: <span className="text-slate-300">http://localhost:8086</span></li>
                </ul>
              </div>

              <h3 className="text-lg font-bold text-white mt-8">Authentication Flow</h3>
              <p className="leading-relaxed">
                Most operations require a secure JSON Web Token (JWT) bearer handshake. To execute protected requests:
              </p>
              <ol className="list-decimal list-inside space-y-2 pl-2">
                <li>Submit credentials to <code className="font-mono text-indigo-300">POST /api/v1/auth/login</code></li>
                <li>Extract the <code className="font-mono text-indigo-300">accessToken</code> from the response payload</li>
                <li>Add the token in your HTTP Request Header: <code className="font-mono text-indigo-300">Authorization: Bearer &lt;token&gt;</code></li>
              </ol>

              <h3 className="text-lg font-bold text-white mt-8">Error Handlers</h3>
              <p className="leading-relaxed mb-4">
                The API utilizes standard RFC 7807 Problem Details representation for structured exceptions:
              </p>
              <pre className="bg-slate-950 border border-white/5 rounded-xl p-4 text-xs font-mono text-slate-300">
{`{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Custom alias 'my-alias' is already in use by another shortened URL.",
  "instance": "/api/v1/urls"
}`}
              </pre>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
