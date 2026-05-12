import { Outlet, Link, useLocation } from 'react-router-dom';
import { Home, Users, Shield, BarChart3, ClipboardList } from 'lucide-react';

const Layout = () => {
  const location = useLocation();

  const navItems = [
    { path: '/', name: 'Dashboard', icon: <Home className="w-5 h-5" /> },
    { path: '/players', name: 'Spieler', icon: <Users className="w-5 h-5" /> },
    { path: '/clubs', name: 'Vereine', icon: <Shield className="w-5 h-5" /> },
    { path: '/analytics', name: 'Analyse', icon: <BarChart3 className="w-5 h-5" /> },
    { path: '/scouting', name: 'Scouting', icon: <ClipboardList className="w-5 h-5" /> },
  ];

  return (
    
    <div className="min-h-screen bg-[#0c0c16] flex flex-col">
      <nav className="bg-[#1a1a2a] border-b border-[#2a2a3a] sticky top-0 z-50 w-full">
        <div className="max-w-7xl mx-auto px-4 w-full">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-2">
              <span className="text-2xl font-bold text-[#6666ff]">⚽</span>
              <span className="text-white font-semibold">Transfermarkt Analyst</span>
            </div>
            <div className="flex gap-6">
              {navItems.map((item) => (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`flex items-center gap-2 text-sm transition-colors ${
                    location.pathname === item.path
                      ? 'text-[#6666ff]'
                      : 'text-[#b8baff] hover:text-white'
                  }`}
                >
                  {item.icon}
                  {item.name}
                </Link>
              ))}
            </div>
          </div>
        </div>
      </nav>
      <main className="flex-1 w-full">
        <div className="max-w-7xl mx-auto px-4 py-8 w-full">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default Layout;