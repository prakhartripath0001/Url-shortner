import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import Navbar from "../components/Navbar/Navbar.jsx";

function HomePage() {
  const { t } = useTranslation();

  useEffect(() => {
    document.title = "Shortify — Shorten, Share, Track";
  }, []);

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <main className="flex flex-col items-center justify-center min-h-[calc(100vh-72px)] px-4 text-center">
        <h1 className="text-5xl font-extrabold tracking-tight text-slate-900 mb-4">
          {t("hero_headline").split(".").map((part, i, arr) =>
            i === arr.length - 2 ? (
              <span key={i}>
                {part}.<span className="text-blue-600"> {arr[arr.length - 1]}</span>
              </span>
            ) : i < arr.length - 2 ? (
              <span key={i}>{part}. </span>
            ) : null
          )}
        </h1>
        <p className="text-lg text-slate-500 max-w-xl mb-8">
          {t("hero_subtitle")}
        </p>
        <div className="flex gap-4">
          <Link
            to="/signup"
            className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
          >
            {t("get_started")}
          </Link>
          <Link
            to="/login"
            className="border border-slate-200 text-slate-700 px-6 py-3 rounded-xl font-semibold hover:border-blue-300 hover:text-blue-600 transition"
          >
            {t("login")}
          </Link>
        </div>
      </main>
    </div>
  );
}

export default HomePage;
