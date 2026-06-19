import { Globe } from "lucide-react";
import { useTranslation } from "react-i18next";

function LanguageSwitcher() {
  const { t, i18n } = useTranslation();

  return (
    <div className="flex items-center gap-2 border rounded-lg px-3 py-2">
      <Globe size={18} className="text-slate-500" />
      <select
        className="outline-none bg-transparent text-sm cursor-pointer"
        value={i18n.language || "en"}
        onChange={(e) => i18n.changeLanguage(e.target.value)}
        aria-label="Select language"
      >
        <option value="en">{t("english")}</option>
        <option value="hi">{t("hindi")}</option>
      </select>
    </div>
  );
}

export default LanguageSwitcher;
