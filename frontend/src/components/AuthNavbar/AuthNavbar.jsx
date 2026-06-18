import { Link } from "react-router-dom";

function AuthNavbar() {
  return (
    <nav className="flex items-center px-8 py-5 border-b bg-white">
      <Link to="/" className="text-2xl font-extrabold tracking-tight">
        Short<span className="text-blue-600">ify</span>
      </Link>
    </nav>
  );
}

export default AuthNavbar;
