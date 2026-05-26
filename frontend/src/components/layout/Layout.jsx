import { Outlet, Link, useLocation } from 'react-router-dom';

const Layout = () => {
  const location = useLocation();

  const navItems = [
    { path: '/', name: 'Dashboard' },
    { path: '/players', name: 'Spieler' },
    { path: '/clubs', name: 'Vereine' },
    { path: '/analytics', name: 'Analyse' },
    { path: '/scouting', name: 'Scouting' },
  ];

  return (
    <div className="layout-container">
      <nav className="navbar">
        <div className="navbar-inner">
          <div className="nav-logo">
            <span>⚽</span>
            <span>Transfermarkt Analyst</span>
          </div>
          <div className="nav-links">
            {navItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className={`nav-link ${location.pathname === item.path ? 'active' : ''}`}
              >
                {item.name}
              </Link>
            ))}
          </div>
        </div>
      </nav>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;