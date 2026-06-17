import { Globe } from "lucide-react";
import { useTranslation } from "react-i18next";

function Navbar() {
  const { t, i18n } = useTranslation();

  return (
    <nav className="flex items-center justify-between px-8 py-5 border-b">
      {/* Logo */}
      <h1 className="text-3xl font-extrabold tracking-tight cursor-pointer">
        {i18n.language === 'hi' ? t('logo') : <>Short<span className="text-blue-600">ify</span></>}
      </h1>

      {/* Navigation */}
      <div className="hidden md:flex items-center gap-8 text-sm font-medium">
        <a href="#" className="hover:text-blue-600 transition">
          {t('platform')}
        </a>

        <a href="#" className="hover:text-blue-600 transition">
          {t('pricing')}
        </a>
      </div>

      {/* Actions */}
      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2 border rounded-lg px-3 py-2">
          <Globe size={18} />
          <select 
            className="outline-none bg-transparent text-sm cursor-pointer"
            value={i18n.language || 'en'}
            onChange={(e) => i18n.changeLanguage(e.target.value)}
          >
            <option value="en">{t('english')}</option>
            <option value="hi">{t('hindi')}</option>
          </select>
        </div>

        <button className="text-sm font-medium hover:text-blue-600">
          {t('login')}
        </button>

        <button className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition">
          {t('signup')}
        </button>
      </div>
    </nav>
  );
}

export default Navbar;