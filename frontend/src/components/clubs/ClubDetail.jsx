import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';

const ClubDetail = () => {
  const { id } = useParams();
  const [club, setClub] = useState(null);
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);

  const formatMarketValue = (value) => {
    if (!value || value === '?') return '?';
    const num = parseFloat(value);
    if (isNaN(num)) return '?';
    if (num >= 1000) return `${(num / 1000).toFixed(1)} Mrd €`;
    return `${Math.round(num)} Mio €`;
  };

  const cleanName = (name) => name?.replace(/ \(\d+\)/, '').replace(/\s+\(\d+\)$/, '').trim() || '?';

  useEffect(() => {
    loadClubData();
  }, [id]);

  const loadClubData = async () => {
    try {
      const [clubRes, playersRes] = await Promise.all([
        axios.get(`http://localhost:8080/api/live/teams/teams/by-id/${id}`),
        axios.get(`http://localhost:8080/api/live/teams/${id}/players`)
      ]);
      
      setClub(clubRes.data);
      
      const playersWithValues = await Promise.all(
        playersRes.data.map(async (player) => {
          try {
            const valueRes = await axios.get(`http://localhost:8080/api/sofifa/player/${player.id}/market-value`);
            return { ...player, market_value: valueRes.data.market_value };
          } catch {
            return { ...player, market_value: '?' };
          }
        })
      );

      setPlayers(playersWithValues);
    } catch (err) {
      console.error('Fehler:', err);
    } finally {
      setLoading(false);
    }
  };

  const totalMarketValue = players.reduce((sum, p) => {
    if (p.market_value && p.market_value !== '?') {
      let num = String(p.market_value).replace('Mio', '').replace('Mrd', '').replace('€', '').trim();
      let floatNum = parseFloat(num);
      if (!isNaN(floatNum)) {
        if (p.market_value.includes('Mrd')) floatNum *= 1000;
        sum += floatNum;
      }
    }
    return sum;
  }, 0);

  const formatTotalValue = (val) => {
    if (val >= 1000) return `${(val / 1000).toFixed(1)} Mrd €`;
    return `${Math.round(val)} Mio €`;
  };

  if (loading) return <div style={{ padding: '2rem', color: 'white' }}>Lade...</div>;
  if (!club) return <div style={{ padding: '2rem', color: 'white' }}>Verein nicht gefunden</div>;

  const positionOrder = { 'Goalkeeper': 1, 'Defender': 2, 'Midfield': 3, 'Attack': 4 };
  const getPositionGroup = (pos) => {
    if (!pos) return 5;
    if (pos.includes('Goalkeeper')) return 1;
    if (pos.includes('Defender')) return 2;
    if (pos.includes('Midfield')) return 3;
    if (pos.includes('Attack')) return 4;
    return 5;
  };

  const sortedPlayers = [...players].sort((a, b) => {
    const groupA = getPositionGroup(a.position);
    const groupB = getPositionGroup(b.position);
    if (groupA !== groupB) return groupA - groupB;
    return (a.name || '').localeCompare(b.name || '');
  });

  const getMainPosition = (pos) => pos?.split(' - ')[0] || '?';

  return (
    <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
      <Link to="/clubs" style={{ color: '#6666ff', textDecoration: 'none', display: 'inline-block', marginBottom: '1rem' }}>
        ← Zurück zu den Ligen
      </Link>

      <div style={{
        display: 'flex',
        alignItems: 'center',
        gap: '1.5rem',
        marginBottom: '2rem',
        backgroundColor: '#1a1a2a',
        padding: '1.5rem',
        borderRadius: '1rem'
      }}>
        {club.logo && <img src={club.logo} alt={club.name} style={{ width: '80px', height: '80px', objectFit: 'contain' }} />}
        <div>
          <h1 style={{ fontSize: '2rem', fontWeight: 'bold', color: 'white', margin: 0 }}>{club.name?.replace(/ \(\d+\)/, '')}</h1>
          <div style={{ display: 'flex', gap: '1rem', marginTop: '0.5rem' }}>
            <span style={{ color: '#b8baff' }}>📋 {players.length} Spieler</span>
            <span style={{ color: '#b8baff' }}>💰 {formatTotalValue(totalMarketValue)}</span>
          </div>
        </div>
      </div>

      <div style={{ backgroundColor: '#1a1a2a', borderRadius: '1rem', overflow: 'hidden' }}>
        <div style={{
          display: 'grid',
          gridTemplateColumns: '2fr 1.5fr 1fr 1.5fr 1.5fr',
          backgroundColor: '#0c0c16',
          padding: '0.75rem 1rem',
          fontWeight: 'bold',
          color: '#b8baff'
        }}>
          <div>Spieler</div>
          <div>Position</div>
          <div>Alter</div>
          <div>Nationalität</div>
          <div>Marktwert</div>
        </div>

        {Object.entries(positionOrder).map(([groupName, groupValue]) => {
          const groupPlayers = sortedPlayers.filter(p => getPositionGroup(p.position) === groupValue);
          if (groupPlayers.length === 0) return null;
          return (
            <div key={groupName}>
              <div style={{ padding: '0.5rem 1rem', backgroundColor: '#0c0c16', color: '#6666ff', fontWeight: 'bold', borderBottom: '1px solid #2a2a3a' }}>
                {groupName}
              </div>
              {groupPlayers.map((player) => (
                <Link key={player.id} to={`/players/${player.id}`} style={{ textDecoration: 'none' }}>
                  <div style={{
                    display: 'grid',
                    gridTemplateColumns: '2fr 1.5fr 1fr 1.5fr 1.5fr',
                    padding: '0.75rem 1rem',
                    borderTop: '1px solid #2a2a3a',
                    color: 'white',
                    alignItems: 'center'
                  }}>
                    <div>{cleanName(player.name)}</div>
                    <div style={{ color: '#b8baff', fontSize: '0.875rem' }}>{getMainPosition(player.position)}</div>
                    <div style={{ color: '#b8baff', fontSize: '0.875rem' }}>{player.age || '?'}</div>
                    <div style={{ color: '#b8baff', fontSize: '0.875rem' }}>{player.nationality || '?'}</div>
                    <div style={{ color: '#b8baff', fontSize: '0.875rem' }}>{player.market_value || '?'}</div>
                  </div>
                </Link>
              ))}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default ClubDetail;