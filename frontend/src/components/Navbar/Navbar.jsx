import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import LanguageSwitcher from "../LanguageSwitcher/LanguageSwitcher.jsx";

function Navbar() {
  const { t, i18n } = useTranslation();

  return (
    <nav className="flex items-center justify-between px-8 py-5 border-b">
      {/* Logo */}
      <Link to="/" className="text-3xl font-extrabold tracking-tight cursor-pointer">
        {i18n.language === "hi" ? t("logo") : <>Short<span className="text-blue-600">ify</span></>}
      </Link>

      {/* Navigation */}
      <div className="hidden md:flex items-center gap-8 text-sm font-medium">
        <a href="#" className="hover:text-blue-600 transition">{t("platform")}</a>
        <a href="#" className="hover:text-blue-600 transition">{t("pricing")}</a>
      </div>

      {/* Actions */}
      <div className="flex items-center gap-4">
        <LanguageSwitcher />
        <Link to="/login" className="text-sm font-medium hover:text-blue-600 transition">
          {t("login")}
        </Link>
        <Link
          to="/signup"
          className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition"
        >
          {t("signup")}
        </Link>
      </div>
    </nav>
  );
}

export default Navbar;