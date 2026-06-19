import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

function NotFoundPage() {
  const { t } = useTranslation();

  useEffect(() => {
    document.title = "404 — Shortify";
  }, []);

  return (
    <div className="min-h-screen bg-white flex flex-col items-center justify-center px-4 text-center">
      <p className="text-8xl font-extrabold text-blue-600 mb-4">404</p>
      <h1 className="text-2xl font-bold text-slate-900 mb-2">{t("page_not_found")}</h1>
      <p className="text-slate-500 mb-8">{t("not_found_message")}</p>
      <Link
        to="/"
        className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
      >
        {t("go_home")}
      </Link>
    </div>
  );
}

export default NotFoundPage;
