import Navbar from '../components/Navbar/Navbar.jsx';

function HomePage() {
  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <main className="flex flex-col items-center justify-center min-h-[calc(100vh-80px)] px-4 text-center">
        <h1 className="text-5xl font-extrabold tracking-tight text-slate-900 mb-4">
          Shorten. Share. <span className="text-blue-600">Track.</span>
        </h1>
        <p className="text-lg text-slate-500 max-w-xl mb-8">
          Create short, powerful links in seconds. Get click analytics, set expiry dates, and manage all your links in one place.
        </p>
        <div className="flex gap-4">
          <a
            href="/signup"
            className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
          >
            Get Started Free
          </a>
          <a
            href="/login"
            className="border border-slate-200 text-slate-700 px-6 py-3 rounded-xl font-semibold hover:border-blue-300 hover:text-blue-600 transition"
          >
            Login
          </a>
        </div>
      </main>
    </div>
  );
}

export default HomePage;
