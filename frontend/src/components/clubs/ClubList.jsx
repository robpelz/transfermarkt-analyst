import { useState, useEffect } from 'react';
import ClubCard from './ClubCard';

const ClubList = () => {
  const [leagues, setLeagues] = useState([]);
  const [teams, setTeams] = useState([]);
  const [selectedLeagueName, setSelectedLeagueName] = useState(null);
  const [loadingTeams, setLoadingTeams] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const topLeagues = [
      { id: 1, name: 'Premier League', country: 'England' },
      { id: 2, name: 'Bundesliga', country: 'Germany' },
      { id: 3, name: 'Serie A', country: 'Italy' },
      { id: 4, name: 'La Liga', country: 'Spain' }
    ];
    setLeagues(topLeagues);
    setSelectedLeagueName(topLeagues[0].name);
  }, []);

  useEffect(() => {
    if (!selectedLeagueName) return;
    
    setLoadingTeams(true);
    setError(null);
    
   fetch(`http://localhost:8080/api/live/teams/by-league?league=${encodeURIComponent(selectedLeagueName)}`)
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then(data => {
        let normalizedTeams = [];
        if (Array.isArray(data)) {
          normalizedTeams = data.map(item => ({
            idTeam: item.id,
            strTeam: item.name,
            localLogoUrl: item.logo,
            strStadium: null,
            intFormedYear: null
          }));
        }
        setTeams(normalizedTeams);
        setLoadingTeams(false);
      })
      .catch(err => {
        console.error('Fehler beim Laden der Teams:', err);
        setError('Teams konnten nicht geladen werden');
        setLoadingTeams(false);
      });
  }, [selectedLeagueName]);

  if (error) return <div style={{ color: '#ef4444', textAlign: 'center', padding: '3rem' }}>{error}</div>;

  return (
    <div>
      <h1 style={{ fontSize: '1.875rem', fontWeight: 'bold', color: 'white', marginBottom: '0.5rem' }}>Vereine</h1>
      <p style={{ color: '#b8baff', marginBottom: '2rem' }}>Top-Ligen und Teams</p>

      <div className="leagues-bar">
        {leagues.map(league => (
          <button
            key={league.id}
            onClick={() => setSelectedLeagueName(league.name)}
            className={`league-button ${selectedLeagueName === league.name ? 'active' : ''}`}
          >
            {league.name}
          </button>
        ))}
      </div>

      {loadingTeams ? (
        <div className="loading-spinner">
          <div className="spinner"></div>
        </div>
      ) : teams.length === 0 ? (
        <div style={{ backgroundColor: '#1a1a2a', border: '1px solid #2a2a3a', borderRadius: '0.75rem', padding: '3rem', textAlign: 'center' }}>
          <p style={{ color: '#b8baff' }}>Keine Teams gefunden</p>
        </div>
      ) : (
        <div className="grid-3">
          {teams.map(team => (
            <ClubCard key={team.idTeam} club={team} />
          ))}
        </div>
      )}
    </div>
  );
};

export default ClubList;