// PlayerList.jsx
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import playerService from '../../services/playerService';

const PlayerList = () => {
  const [query, setQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [randomPlayers, setRandomPlayers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadRandomPlayers();
  }, []);

  const loadRandomPlayers = async () => {
    setLoading(true);
    const players = await playerService.getRandomPlayers();
    setRandomPlayers(players);
    setLoading(false);
  };

  useEffect(() => {
    if (query.length < 2) {
      setSuggestions([]);
      return;
    }
    const delay = setTimeout(() => searchPlayers(), 300);
    return () => clearTimeout(delay);
  }, [query]);

  const searchPlayers = async () => {
    setLoading(true);
    const results = await playerService.searchPlayers(query);
    setSuggestions(results.slice(0, 8));
    setLoading(false);
  };

  const cleanName = (name) => name?.replace(/ \(\d+\)/, '') || '?';
  
  const formatValue = (val) => {
    if (!val || val === '?') return '?';
    let num = String(val).replace('€', '').replace('Mio', '').replace('.', '').trim();
    const floatNum = parseFloat(num);
    if (isNaN(floatNum)) return '?';
    if (floatNum >= 1000) return `${(floatNum / 1000).toFixed(1)} Mrd €`;
    return `${Math.round(floatNum)} Mio €`;
  };

  const renderPlayerCards = (players) => (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '1rem' }}>
      {players.map((player) => (
        <Link key={player.id} to={`/players/${player.id}`} style={{ textDecoration: 'none' }}>
          <div style={{
            backgroundColor: '#1a1a2a',
            border: '1px solid #2a2a3a',
            borderRadius: '0.5rem',
            padding: '1rem',
            transition: '0.2s'
          }}>
            <div style={{ fontWeight: 'bold', color: 'white' }}>{cleanName(player.name)}</div>
            <div style={{ color: '#b8baff', fontSize: '0.875rem' }}>{player.club || '?'}</div>
            <div style={{ color: '#b8baff', fontSize: '0.75rem', marginTop: '0.5rem' }}>
              {player.age} Jahre | {formatValue(player.value)}
            </div>
          </div>
        </Link>
      ))}
    </div>
  );

  return (
    <div style={{ backgroundColor: '#0c0c16', minHeight: '100vh', padding: '2rem' }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <h1 style={{ fontSize: '2rem', fontWeight: 'bold', color: 'white', marginBottom: '0.5rem' }}>Spieler-Suche</h1>
        <p style={{ color: '#b8baff', marginBottom: '2rem' }}>Suche nach Spielern in der Datenbank</p>

        <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem' }}>
          <input
            type="text"
            placeholder="Spieler suchen (z.B. Wirtz, Haaland)"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            style={{
              flex: 1,
              padding: '1rem',
              fontSize: '1rem',
              backgroundColor: '#1a1a2a',
              border: '1px solid #2a2a3a',
              borderRadius: '0.5rem',
              color: 'white'
            }}
          />
          <button
            onClick={searchPlayers}
            disabled={loading}
            style={{
              padding: '0 2rem',
              backgroundColor: '#6666ff',
              color: 'white',
              border: 'none',
              borderRadius: '0.5rem',
              cursor: loading ? 'not-allowed' : 'pointer'
            }}
          >
            {loading ? 'Lädt...' : 'Suchen'}
          </button>
        </div>

        {query.length < 2 && randomPlayers.length > 0 && (
          <>
            <h2 style={{ color: 'white', marginBottom: '1rem' }}>Beliebte Spieler</h2>
            {renderPlayerCards(randomPlayers)}
          </>
        )}

        {suggestions.length > 0 && (
          <>
            <h2 style={{ color: 'white', marginBottom: '1rem' }}>Suchergebnisse</h2>
            {renderPlayerCards(suggestions)}
          </>
        )}
      </div>
    </div>
  );
};

export default PlayerList;