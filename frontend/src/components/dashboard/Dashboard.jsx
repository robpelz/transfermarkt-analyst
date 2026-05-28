import { Link } from 'react-router-dom';

const Dashboard = () => {
  // Feste Demo-Daten – funktionieren immer, kein API-Call
  const stats = {
    totalPlayers: 92671,
    totalClubs: 4,
    totalTransfers: 456,
    averageScore: 72,
    topPlayers: [
      { id: 598577, name: 'Florian Wirtz', club: 'Liverpool', score: 88 },
      { id: 418560, name: 'Erling Haaland', club: 'Manchester City', score: 92 },
      { id: 581678, name: 'Jude Bellingham', club: 'Real Madrid', score: 86 },
      { id: 580195, name: 'Jamal Musiala', club: 'Bayern Munich', score: 85 },
      { id: 1064378, name: 'Harry Kane', club: 'Bayern Munich', score: 89 }
    ],
    topClubs: [
      { id: 1, name: 'Premier League', score: 94 },
      { id: 2, name: 'Bundesliga', score: 92 },
      { id: 3, name: 'La Liga', score: 88 }
    ]
  };

  return (
    <div>
      {/* Welcome Section */}
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '2.25rem', fontWeight: 'bold', color: 'white', marginBottom: '0.5rem' }}>
          Welcome back, <span style={{ color: '#6666ff' }}>Analyst</span>
        </h1>
        <p style={{ color: '#b8baff', fontSize: '1.125rem' }}>
          Here's what's happening with your transfer analysis
        </p>
      </div>

      {/* Stats Grid */}
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(4, 1fr)', 
        gap: '1.5rem', 
        marginBottom: '2rem' 
      }}>
        <StatCard title="Total Players" value={stats.totalPlayers} color="#3b82f6" />
        <StatCard title="Total Clubs" value={stats.totalClubs} color="#8b5cf6" />
        <StatCard title="Transfers" value={stats.totalTransfers} color="#10b981" />
        <StatCard title="Avg. Score" value={`${stats.averageScore}%`} color="#f59e0b" />
      </div>

      {/* Two Column Layout */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
        
        {/* Top Players */}
        <div style={{ backgroundColor: '#1a1a2a', border: '1px solid #2a2a3a', borderRadius: '0.75rem', padding: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <h2 style={{ fontSize: '1.25rem', fontWeight: '600', color: 'white' }}>Top Players</h2>
            <Link to="/players" style={{ color: '#6666ff', fontSize: '0.875rem', textDecoration: 'none' }}>View All →</Link>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {stats.topPlayers.map((player, index) => (
              <Link key={player.id} to={`/players/${player.id}`} style={{ textDecoration: 'none' }}>
                <div style={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  justifyContent: 'space-between', 
                  padding: '0.75rem', 
                  backgroundColor: '#0c0c16', 
                  borderRadius: '0.5rem'
                }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <span style={{ color: '#b8baff' }}>#{index + 1}</span>
                    <div>
                      <div style={{ color: 'white', fontWeight: '500' }}>{player.name}</div>
                      <div style={{ color: '#b8baff', fontSize: '0.875rem' }}>{player.club}</div>
                    </div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span style={{ color: '#6666ff', fontWeight: 'bold' }}>{player.score}</span>
                    <span>⭐</span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>

        {/* Top Clubs */}
        <div style={{ backgroundColor: '#1a1a2a', border: '1px solid #2a2a3a', borderRadius: '0.75rem', padding: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <h2 style={{ fontSize: '1.25rem', fontWeight: '600', color: 'white' }}>Top Clubs</h2>
            <Link to="/clubs" style={{ color: '#6666ff', fontSize: '0.875rem', textDecoration: 'none' }}>View All →</Link>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {stats.topClubs.map((club, index) => (
              <Link key={club.id} to="/clubs" style={{ textDecoration: 'none' }}>
                <div style={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  justifyContent: 'space-between', 
                  padding: '0.75rem', 
                  backgroundColor: '#0c0c16', 
                  borderRadius: '0.5rem'
                }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <span style={{ color: '#b8baff' }}>#{index + 1}</span>
                    <div>
                      <div style={{ color: 'white', fontWeight: '500' }}>{club.name}</div>
                      <div style={{ color: '#b8baff', fontSize: '0.875rem' }}>Success Rate</div>
                    </div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span style={{ color: '#6666ff', fontWeight: 'bold' }}>{club.score}%</span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

const StatCard = ({ title, value, color }) => {
  return (
    <div style={{
      backgroundColor: '#1a1a2a',
      border: '1px solid #2a2a3a',
      borderRadius: '0.75rem',
      padding: '1.5rem'
    }}>
      <div style={{
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        width: '3rem',
        height: '3rem',
        borderRadius: '0.5rem',
        marginBottom: '1rem',
        backgroundColor: `${color}20`
      }}>
        <div style={{
          width: '1.5rem',
          height: '1.5rem',
          backgroundColor: color,
          borderRadius: '0.25rem'
        }}></div>
      </div>
      <div style={{ fontSize: '2rem', fontWeight: 'bold', color: 'white', marginBottom: '0.25rem' }}>{value}</div>
      <div style={{ color: '#b8baff', fontSize: '0.875rem' }}>{title}</div>
      <div style={{ fontSize: '0.75rem', color: '#10b981', backgroundColor: 'rgba(16, 185, 129, 0.1)', padding: '0.25rem 0.5rem', borderRadius: '0.25rem', display: 'inline-block', marginTop: '0.5rem' }}>+12% this month</div>
    </div>
  );
};

export default Dashboard;