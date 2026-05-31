// PlayerList.jsx
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import playerService from '../../services/playerService';

const PlayerList = () => {
  const [query, setQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [popularPlayers, setPopularPlayers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searching, setSearching] = useState(false);
  const [debouncedQuery, setDebouncedQuery] = useState('');

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedQuery(query);
    }, 500);
    return () => clearTimeout(timer);
  }, [query]);

  const popularNames = ['Wirtz', 'Haaland', 'Musiala'];

  useEffect(() => {
    loadPopularPlayers();
  }, []);

  const loadPopularPlayers = async () => {
    setLoading(true);
    try {
      const results = await Promise.all(
        popularNames.map(name => playerService.searchPlayers(name))
      );
      const players = results
        .filter(result => result.length > 0)
        .map(result => result[0])
        .slice(0, 3);
      setPopularPlayers(players);
    } catch (error) {
      console.error('Fehler:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const search = async () => {
      if (debouncedQuery.length < 2) {
        setSuggestions([]);
        return;
      }
      setSearching(true);
      try {
        const results = await playerService.searchPlayers(debouncedQuery);
        setSuggestions(results.slice(0, 6));
      } catch (error) {
        console.error('Fehler bei Suche:', error);
      } finally {
        setSearching(false);
      }
    };
    search();
  }, [debouncedQuery]);

  const cleanName = (name) => name?.replace(/ \(\d+\)/, '') || '?';
  
  const formatValue = (val) => {
    if (!val || val === '?') return '?';
    const match = val.match(/(\d+(?:\.\d+)?)/);
    if (!match) return '?';
    const num = parseFloat(match[1]);
    if (val.includes('Mrd')) return `${num} Mrd €`;
    if (val.includes('Mio')) return `${Math.round(num)} Mio €`;
    return `${Math.round(num)} €`;
  };

  const renderPlayerCards = (players, columns = 3) => (
    <div style={{
      display: 'grid',
      gridTemplateColumns: `repeat(${columns}, 1fr)`,
      gap: '24px',
      marginTop: '20px'
    }}>
      {players.map((player) => (
        <Link key={player.id} to={`/players/${player.id}`} style={{ textDecoration: 'none' }}>
          <div style={{
            backgroundColor: '#1a1a2a',
            border: '1px solid #2a2a3a',
            borderRadius: '16px',
            padding: '24px',
            transition: 'transform 0.2s',
            cursor: 'pointer'
          }}
          onMouseEnter={(e) => e.currentTarget.style.transform = 'translateY(-4px)'}
          onMouseLeave={(e) => e.currentTarget.style.transform = 'translateY(0)'}>
            <div style={{ fontWeight: 'bold', color: 'white', fontSize: '18px', marginBottom: '8px' }}>
              {cleanName(player.name)}
            </div>
            <div style={{ color: '#b8baff', fontSize: '14px', marginBottom: '12px' }}>
              {player.club || '?'}
            </div>
            <div style={{ color: '#b8baff', fontSize: '13px' }}>
              {player.age || '?'} Jahre | {formatValue(player.value)}
            </div>
          </div>
        </Link>
      ))}
    </div>
  );

  return (
    <div style={{ backgroundColor: '#0c0c16', minHeight: '100vh', padding: '24px' }}>
      <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
        <h1 style={{ fontSize: '28px', fontWeight: 'bold', color: 'white', marginBottom: '8px' }}>Spieler-Suche</h1>
        <p style={{ color: '#b8baff', marginBottom: '24px' }}>Suche nach Spielern in der Datenbank</p>

        <div style={{ display: 'flex', gap: '12px', marginBottom: '32px' }}>
          <input
            type="text"
            placeholder="Spieler suchen (z.B. Wirtz, Haaland)"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && setDebouncedQuery(query)}
            style={{
              flex: 1,
              padding: '12px 16px',
              fontSize: '16px',
              backgroundColor: '#1a1a2a',
              border: '1px solid #2a2a3a',
              borderRadius: '8px',
              color: 'white',
              outline: 'none'
            }}
          />
        </div>

        {suggestions.length > 0 && (
          <>
            <h2 style={{ color: 'white', fontSize: '20px', marginBottom: '16px' }}>
              🔍 Suchergebnisse für "{query}"
            </h2>
            {renderPlayerCards(suggestions, 3)}
          </>
        )}

        {query.length >= 2 && suggestions.length === 0 && !searching && (
          <div style={{
            backgroundColor: '#1a1a2a',
            border: '1px solid #2a2a3a',
            borderRadius: '12px',
            padding: '48px',
            textAlign: 'center'
          }}>
            <p style={{ color: '#b8baff' }}>Keine Spieler gefunden für "{query}"</p>
          </div>
        )}

        {query.length < 2 && (
          <>
            <h2 style={{ color: 'white', fontSize: '20px', marginBottom: '16px' }}>
              ⭐ Beliebte Spieler
            </h2>
            {loading ? (
              <div style={{ textAlign: 'center', padding: '48px', color: '#b8baff' }}>
                Lade Spieler...
              </div>
            ) : (
              renderPlayerCards(popularPlayers, 3)
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default PlayerList;