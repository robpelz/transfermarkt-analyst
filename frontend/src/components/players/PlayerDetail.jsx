// PlayerDetail.jsx
import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import playerService from '../../services/playerService';

const PlayerDetail = () => {
  const { id } = useParams();
  const [player, setPlayer] = useState(null);
  const [score, setScore] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [imageError, setImageError] = useState(false);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [playerData, scoreData] = await Promise.all([
          playerService.getById(id),
          playerService.getTransferScore(id)
        ]);
        
        console.log('📦 Player Data:', playerData);
        console.log('🖼️ Image URL:', playerData?.imageUrl);
        
        setPlayer(playerData);
        setScore(scoreData);
        setImageError(false);
      } catch (err) {
        console.error('❌ Fehler:', err);
        setError('Spieler nicht gefunden');
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [id]);

  const getScoreColor = (value) => {
    if (value >= 70) return '#10b981';
    if (value >= 50) return '#eab308';
    return '#ef4444';
  };

  if (loading) {
    return (
      <div style={{ background: '#0c0c16', minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white' }}>
        Lade...
      </div>
    );
  }

  if (error || !player) {
    return (
      <div style={{ background: '#0c0c16', minHeight: '100vh', padding: '20px', color: 'white' }}>
        {error || 'Spieler nicht gefunden'}
        <Link to="/players" style={{ color: '#6666ff', display: 'block', marginTop: '10px' }}>← Zurück</Link>
      </div>
    );
  }

  console.log('🎨 Rendering with imageUrl:', player.imageUrl);

  return (
    <div style={{ background: '#0c0c16', minHeight: '100vh', padding: '20px' }}>
      <div style={{ maxWidth: '600px', margin: '0 auto' }}>
        
        <Link to="/players" style={{ color: '#b8baff', textDecoration: 'none', marginBottom: '20px', display: 'inline-block' }}>
          ← Zurück
        </Link>

        {/* Player Card */}
        <div style={{ background: '#1a1a2a', borderRadius: '16px', padding: '20px', marginBottom: '20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '20px' }}>
            
            {/* Avatar */}
            <div style={{
              width: '80px',
              height: '80px',
              borderRadius: '50%',
              backgroundColor: '#2a2a3a',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              overflow: 'hidden',
              flexShrink: 0
            }}>
              {!imageError && player.imageUrl ? (
                <img 
                  src={player.imageUrl}
                  alt={player.name}
                  style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                  onLoad={() => console.log('✅ Bild geladen:', player.imageUrl)}
                  onError={() => {
                    console.log('❌ Bild fehlgeschlagen:', player.imageUrl);
                    setImageError(true);
                  }}
                />
              ) : (
                <span style={{ fontSize: '36px' }}>⚽</span>
              )}
            </div>
            
            {/* Name und Info */}
            <div>
              <h1 style={{ color: 'white', fontSize: '20px', margin: 0 }}>{player.name}</h1>
              <div style={{ marginTop: '4px' }}>
                <span style={{ color: '#b8baff', fontSize: '13px' }}>{player.positions?.[0] || player.position || 'N/A'}</span>
                <span style={{ color: '#b8baff', fontSize: '13px', marginLeft: '8px' }}>•</span>
                <span style={{ color: '#b8baff', fontSize: '13px', marginLeft: '8px' }}>{player.club || '?'}</span>
              </div>
            </div>
            
            {/* Score */}
            <div style={{ marginLeft: 'auto', textAlign: 'center' }}>
              <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#6666ff' }}>{score?.totalScore || 0}</div>
              <div style={{ fontSize: '10px', color: '#b8baff' }}>Score</div>
            </div>
          </div>

          {/* Stats Row */}
          <div style={{ display: 'flex', gap: '12px', marginBottom: '20px' }}>
            <div style={{ flex: 1, background: '#0c0c16', borderRadius: '12px', padding: '10px', textAlign: 'center' }}>
              <div style={{ fontSize: '11px', color: '#b8baff' }}>Alter</div>
              <div style={{ fontSize: '20px', fontWeight: 'bold', color: 'white' }}>{player.age || '?'}</div>
            </div>
            <div style={{ flex: 1, background: '#0c0c16', borderRadius: '12px', padding: '10px', textAlign: 'center' }}>
              <div style={{ fontSize: '11px', color: '#b8baff' }}>Nationalität</div>
              <div style={{ fontSize: '20px', fontWeight: 'bold', color: 'white' }}>{player.nationality?.substring(0, 3) || '?'}</div>
            </div>
            <div style={{ flex: 1, background: '#0c0c16', borderRadius: '12px', padding: '10px', textAlign: 'center' }}>
              <div style={{ fontSize: '11px', color: '#b8baff' }}>Marktwert</div>
              <div style={{ fontSize: '16px', fontWeight: 'bold', color: 'white' }}>{player.value || '?'}</div>
            </div>
          </div>

          {/* Score Bar */}
          <div style={{ background: '#0c0c16', borderRadius: '12px', padding: '12px' }}>
            <div style={{ fontSize: '14px', color: 'white', marginBottom: '8px' }}>
              {score?.totalScore >= 80 ? '🔥 Top-Transfer!' : score?.totalScore >= 65 ? '✅ Gutes Investment' : '⚠️ Solide'}
            </div>
            <div style={{ background: '#2a2a3a', borderRadius: '4px', height: '6px' }}>
              <div style={{ width: `${score?.totalScore || 0}%`, background: getScoreColor(score?.totalScore || 0), height: '6px', borderRadius: '4px' }} />
            </div>
          </div>
        </div>

        {/* Score Details */}
        <div style={{ background: '#1a1a2a', borderRadius: '16px', padding: '20px' }}>
          <h3 style={{ color: 'white', fontSize: '18px', marginBottom: '16px' }}>Detailanalyse</h3>
          
          <div style={{ marginBottom: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
              <span style={{ fontSize: '13px', color: '#b8baff' }}>Position</span>
              <span style={{ fontSize: '13px', fontWeight: 'bold', color: 'white' }}>{score?.positionScore || 0}%</span>
            </div>
            <div style={{ background: '#2a2a3a', borderRadius: '4px', height: '8px', overflow: 'hidden' }}>
              <div style={{ width: `${score?.positionScore || 0}%`, background: '#6666ff', height: '8px', borderRadius: '4px' }} />
            </div>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
              <span style={{ fontSize: '13px', color: '#b8baff' }}>Preis</span>
              <span style={{ fontSize: '13px', fontWeight: 'bold', color: 'white' }}>{score?.priceScore || 0}%</span>
            </div>
            <div style={{ background: '#2a2a3a', borderRadius: '4px', height: '8px', overflow: 'hidden' }}>
              <div style={{ width: `${score?.priceScore || 0}%`, background: '#10b981', height: '8px', borderRadius: '4px' }} />
            </div>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
              <span style={{ fontSize: '13px', color: '#b8baff' }}>Alter</span>
              <span style={{ fontSize: '13px', fontWeight: 'bold', color: 'white' }}>{score?.ageScore || 0}%</span>
            </div>
            <div style={{ background: '#2a2a3a', borderRadius: '4px', height: '8px', overflow: 'hidden' }}>
              <div style={{ width: `${score?.ageScore || 0}%`, background: '#eab308', height: '8px', borderRadius: '4px' }} />
            </div>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
              <span style={{ fontSize: '13px', color: '#b8baff' }}>Erfahrung</span>
              <span style={{ fontSize: '13px', fontWeight: 'bold', color: 'white' }}>{score?.experienceScore || 0}%</span>
            </div>
            <div style={{ background: '#2a2a3a', borderRadius: '4px', height: '8px', overflow: 'hidden' }}>
              <div style={{ width: `${score?.experienceScore || 0}%`, background: '#f97316', height: '8px', borderRadius: '4px' }} />
            </div>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
              <span style={{ fontSize: '13px', color: '#b8baff' }}>Liga</span>
              <span style={{ fontSize: '13px', fontWeight: 'bold', color: 'white' }}>{score?.competitionScore || 0}%</span>
            </div>
            <div style={{ background: '#2a2a3a', borderRadius: '4px', height: '8px', overflow: 'hidden' }}>
              <div style={{ width: `${score?.competitionScore || 0}%`, background: '#8b5cf6', height: '8px', borderRadius: '4px' }} />
            </div>
          </div>
        </div>

        {/* Info Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px', marginTop: '16px' }}>
          <div style={{ background: '#1a1a2a', borderRadius: '12px', padding: '12px', textAlign: 'center', border: '1px solid #2a2a3a' }}>
            <div style={{ fontSize: '20px' }}>📈</div>
            <div style={{ fontSize: '12px', color: 'white' }}>{player.age < 23 ? 'Hohes Potenzial' : player.age < 28 ? 'Im Aufbau' : 'Erfahren'}</div>
          </div>
          <div style={{ background: '#1a1a2a', borderRadius: '12px', padding: '12px', textAlign: 'center', border: '1px solid #2a2a3a' }}>
            <div style={{ fontSize: '20px' }}>🛡️</div>
            <div style={{ fontSize: '12px', color: 'white' }}>{player.age > 28 ? 'Leader' : player.age > 23 ? 'Etabliert' : 'Talent'}</div>
          </div>
          <div style={{ background: '#1a1a2a', borderRadius: '12px', padding: '12px', textAlign: 'center', border: '1px solid #2a2a3a' }}>
            <div style={{ fontSize: '20px' }}>⭐</div>
            <div style={{ fontSize: '12px', color: 'white' }}>{player.age >= 25 && player.age <= 31 ? 'Prime' : player.age < 25 ? 'Frühphase' : 'Spätphase'}</div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default PlayerDetail;