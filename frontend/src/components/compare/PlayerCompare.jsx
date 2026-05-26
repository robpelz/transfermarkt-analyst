import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Trophy, X, Sparkles, Target, Activity } from 'lucide-react';
import { RadarChart, PolarGrid, PolarAngleAxis, Radar, ResponsiveContainer, Legend } from 'recharts';
import axios from 'axios';
import playerService from '../../services/playerService';

const WinnerConfetti = () => {
  const [pieces, setPieces] = useState([]);
  
  useEffect(() => {
    const newPieces = [];
    for (let i = 0; i < 100; i++) {
      newPieces.push({
        id: i,
        left: Math.random() * 100 + '%',
        animationDuration: 1 + Math.random() * 2,
        animationDelay: Math.random() * 0.5,
      });
    }
    setPieces(newPieces);
    const timer = setTimeout(() => setPieces([]), 3000);
    return () => clearTimeout(timer);
  }, []);
  
  return (
    <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, pointerEvents: 'none', zIndex: 1000, overflow: 'hidden' }}>
      {pieces.map(piece => (
        <div
          key={piece.id}
          style={{
            position: 'absolute',
            left: piece.left,
            top: '-10%',
            width: '10px',
            height: '20px',
            backgroundColor: ['#fbbf24', '#f59e0b', '#d97706', '#fcd34d'][Math.floor(Math.random() * 4)],
            transform: 'rotate(' + Math.random() * 360 + 'deg)',
            animation: `fall ${piece.animationDuration}s linear ${piece.animationDelay}s forwards`,
          }}
        />
      ))}
      <style>{`
        @keyframes fall {
          0% { transform: translateY(-10vh) rotate(0deg); opacity: 1; }
          100% { transform: translateY(100vh) rotate(360deg); opacity: 0; }
        }
      `}</style>
    </div>
  );
};

const SparkleEffect = () => (
  <div style={{ position: 'absolute', top: '-10px', right: '-10px', animation: 'sparkle 0.5s ease-in-out infinite' }}>
    <Sparkles className="w-6 h-6" style={{ color: '#fbbf24' }} />
    <style>{`
      @keyframes sparkle {
        0%, 100% { transform: scale(1); opacity: 1; }
        50% { transform: scale(1.3); opacity: 0.7; }
      }
    `}</style>
  </div>
);

const PlayerCompare = () => {
  const [player1, setPlayer1] = useState(null);
  const [player2, setPlayer2] = useState(null);
  const [search1, setSearch1] = useState('');
  const [search2, setSearch2] = useState('');
  const [suggestions1, setSuggestions1] = useState([]);
  const [suggestions2, setSuggestions2] = useState([]);
  const [loading1, setLoading1] = useState(false);
  const [loading2, setLoading2] = useState(false);
  const [showComparison, setShowComparison] = useState(false);
  const [marketValue1, setMarketValue1] = useState(null);
  const [marketValue2, setMarketValue2] = useState(null);
  const [score1, setScore1] = useState(null);
  const [score2, setScore2] = useState(null);
  const [showWinnerAnim, setShowWinnerAnim] = useState(false);

  const cleanName = (name) => name?.replace(/ \(\d+\)/, '') || '?';
  
  const formatMarketValue = (value) => {
    if (!value || value === '?') return '?';
    const num = parseFloat(value);
    if (isNaN(num)) return '?';
    if (num >= 1000) return `${(num / 1000).toFixed(1)} Mrd €`;
    return `${Math.round(num)} Mio €`;
  };

  // Debounced search für Spieler 1
  useEffect(() => {
    if (search1.length < 2) {
      setSuggestions1([]);
      return;
    }
    const timer = setTimeout(async () => {
      setLoading1(true);
      try {
        const results = await playerService.searchPlayers(search1);
        setSuggestions1(results.slice(0, 5));
      } catch (error) {
        console.error('Fehler:', error);
      } finally {
        setLoading1(false);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [search1]);

  // Debounced search für Spieler 2
  useEffect(() => {
    if (search2.length < 2) {
      setSuggestions2([]);
      return;
    }
    const timer = setTimeout(async () => {
      setLoading2(true);
      try {
        const results = await playerService.searchPlayers(search2);
        setSuggestions2(results.slice(0, 5));
      } catch (error) {
        console.error('Fehler:', error);
      } finally {
        setLoading2(false);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [search2]);

  const selectPlayer1 = async (player) => {
    setPlayer1(player);
    setSuggestions1([]);
    setSearch1(player.name);
    try {
      const valueRes = await axios.get(`http://localhost:8080/api/sofifa/player/${player.id}/market-value`);
      setMarketValue1(valueRes.data.market_value);
      const scoreRes = await axios.get(`http://localhost:8080/api/sofifa/player/${player.id}/score`);
      setScore1(scoreRes.data);
    } catch (err) {
      setMarketValue1('?');
      setScore1(null);
    }
  };

  const selectPlayer2 = async (player) => {
    setPlayer2(player);
    setSuggestions2([]);
    setSearch2(player.name);
    try {
      const valueRes = await axios.get(`http://localhost:8080/api/sofifa/player/${player.id}/market-value`);
      setMarketValue2(valueRes.data.market_value);
      const scoreRes = await axios.get(`http://localhost:8080/api/sofifa/player/${player.id}/score`);
      setScore2(scoreRes.data);
    } catch (err) {
      setMarketValue2('?');
      setScore2(null);
    }
  };

  const removePlayer1 = () => {
    setPlayer1(null);
    setSearch1('');
    setMarketValue1(null);
    setScore1(null);
    setShowComparison(false);
  };

  const removePlayer2 = () => {
    setPlayer2(null);
    setSearch2('');
    setMarketValue2(null);
    setScore2(null);
    setShowComparison(false);
  };

  const startComparison = () => {
    if (player1 && player2) {
      setShowWinnerAnim(false);
      setShowComparison(true);
      setTimeout(() => setShowWinnerAnim(true), 100);
    }
  };

  const getRadarData = () => {
    if (!player1 || !player2) return [];
    const val1 = parseFloat(marketValue1) || 0;
    const val2 = parseFloat(marketValue2) || 0;
    const maxValue = Math.max(val1, val2, 100);
    const normalizedVal1 = (val1 / maxValue) * 100;
    const normalizedVal2 = (val2 / maxValue) * 100;
    
    return [
      { category: 'Alter', player1: player1.age ? 100 - Math.min(100, (player1.age / 40) * 100) : 50, player2: player2.age ? 100 - Math.min(100, (player2.age / 40) * 100) : 50 },
      { category: 'Marktwert', player1: normalizedVal1, player2: normalizedVal2 },
      { category: 'Score', player1: score1?.totalScore || 0, player2: score2?.totalScore || 0 },
      { category: 'Talent', player1: 85, player2: 82 },
      { category: 'Speed', player1: 88, player2: 85 },
      { category: 'Technik', player1: 86, player2: 84 },
    ];
  };

  const getWinner = () => {
    const totalScore1 = score1?.totalScore || 0;
    const totalScore2 = score2?.totalScore || 0;
    if (totalScore1 > totalScore2) return player1;
    if (totalScore2 > totalScore1) return player2;
    return null;
  };

  const winner = getWinner();
  const radarData = getRadarData();

  const renderPlayerCard = (player, title, onRemove, isPlayerOne) => {
    const isWinner = winner && player && winner.id === player.id;
    const showWinnerScore = showComparison && isWinner;
    const currentScore = isPlayerOne ? score1 : score2;
    const marketValue = isPlayerOne ? marketValue1 : marketValue2;
    const suggestions = isPlayerOne ? suggestions1 : suggestions2;
    const loading = isPlayerOne ? loading1 : loading2;
    const searchValue = isPlayerOne ? search1 : search2;
    const setSearchValue = isPlayerOne ? setSearch1 : setSearch2;
    const selectPlayer = isPlayerOne ? selectPlayer1 : selectPlayer2;

    return (
      <div style={{ 
        backgroundColor: isWinner && showComparison ? '#d97706' : '#1a1a2a', 
        border: isWinner && showComparison ? '2px solid #fbbf24' : '1px solid #2a2a3a', 
        borderRadius: '0.75rem', 
        padding: '1.5rem', 
        height: '100%',
        position: 'relative',
        boxShadow: isWinner && showComparison ? '0 0 20px rgba(217, 119, 6, 0.5)' : 'none',
        transition: 'all 0.3s ease'
      }}>
        {isWinner && showComparison && <SparkleEffect />}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: isWinner && showComparison ? '#fef3c7' : 'white' }}>{title}</h3>
          {onRemove && (
            <button onClick={onRemove} style={{ color: '#b8baff', background: 'none', border: 'none', cursor: 'pointer' }}>
              <X className="w-5 h-5" />
            </button>
          )}
        </div>
        
        {player ? (
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
              <div style={{ width: '64px', height: '64px', borderRadius: '50%', background: 'linear-gradient(135deg, #6666ff, #b8baff)', display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden' }}>
                <img 
                  src={`https://tmssl.akamaized.net/images/portrait/header/${player.id}.png`}
                  alt={player.name}
                  style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                  onError={(e) => { e.target.onerror = null; e.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(player.name)}&background=6666ff&color=fff&size=64`; }}
                />
              </div>
              <div>
                <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: isWinner && showComparison ? '#fef3c7' : 'white' }}>{cleanName(player.name)}</div>
                <div style={{ color: isWinner && showComparison ? '#fef3c7' : '#b8baff', fontSize: '0.875rem' }}>{player.club}</div>
              </div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '0.75rem' }}>
              <div style={{ backgroundColor: isWinner && showComparison ? 'rgba(0,0,0,0.2)' : '#0c0c16', borderRadius: '0.5rem', padding: '0.5rem', textAlign: 'center' }}>
                <div style={{ color: isWinner && showComparison ? '#fef3c7' : 'white', fontWeight: 'bold' }}>{player.age || '?'}</div>
                <div style={{ color: isWinner && showComparison ? '#fef3c7' : '#b8baff', fontSize: '0.75rem' }}>Alter</div>
              </div>
              <div style={{ backgroundColor: isWinner && showComparison ? 'rgba(0,0,0,0.2)' : '#0c0c16', borderRadius: '0.5rem', padding: '0.5rem', textAlign: 'center' }}>
                <div style={{ color: isWinner && showComparison ? '#fef3c7' : 'white', fontWeight: 'bold' }}>{formatMarketValue(marketValue)}</div>
                <div style={{ color: isWinner && showComparison ? '#fef3c7' : '#b8baff', fontSize: '0.75rem' }}>Marktwert</div>
              </div>
              <div style={{ backgroundColor: isWinner && showComparison ? 'rgba(0,0,0,0.2)' : '#0c0c16', borderRadius: '0.5rem', padding: '0.5rem', textAlign: 'center' }}>
                <div style={{ color: isWinner && showComparison ? '#fef3c7' : 'white', fontWeight: 'bold' }}>{player.nationality || '?'}</div>
                <div style={{ color: isWinner && showComparison ? '#fef3c7' : '#b8baff', fontSize: '0.75rem' }}>Nationalität</div>
              </div>
              <div style={{ backgroundColor: isWinner && showComparison ? 'rgba(0,0,0,0.2)' : '#0c0c16', borderRadius: '0.5rem', padding: '0.5rem', textAlign: 'center' }}>
                <div style={{ color: isWinner && showComparison ? '#fef3c7' : 'white', fontWeight: 'bold' }}>{player.position?.split(' - ')[0] || '?'}</div>
                <div style={{ color: isWinner && showComparison ? '#fef3c7' : '#b8baff', fontSize: '0.75rem' }}>Position</div>
              </div>
            </div>
            <div style={{ 
              marginTop: '1rem', 
              paddingTop: '1rem', 
              borderTop: '1px solid rgba(255,255,255,0.2)', 
              textAlign: 'center',
              backgroundColor: showWinnerScore ? 'rgba(0,0,0,0.2)' : 'transparent',
              borderRadius: '0.5rem',
              padding: '0.5rem',
              transition: 'all 0.3s ease'
            }}>
              <span style={{ fontSize: '0.875rem', color: isWinner && showComparison ? '#fef3c7' : '#b8baff' }}>TransferScore: </span>
              <span style={{ 
                fontSize: '1.5rem', 
                fontWeight: 'bold', 
                color: isWinner && showComparison ? '#fef3c7' : '#6666ff',
                textShadow: showWinnerScore ? '0 0 5px rgba(0,0,0,0.3)' : 'none'
              }}>
                {currentScore?.totalScore || '?'}
              </span>
              <span style={{ fontSize: '0.875rem', color: isWinner && showComparison ? '#fef3c7' : '#b8baff' }}> / 100</span>
              {showWinnerScore && (
                <div style={{ fontSize: '0.7rem', color: '#fef3c7', marginTop: '0.25rem', fontWeight: 'bold' }}>
                  🏆 SIEGER! 🏆
                </div>
              )}
            </div>
          </div>
        ) : (
          <div style={{ position: 'relative' }}>
            <div style={{ marginBottom: '0.75rem' }}>
              <input
                type="text"
                placeholder="Spieler suchen..."
                value={searchValue}
                onChange={(e) => setSearchValue(e.target.value)}
                style={{ width: '100%', padding: '0.5rem 1rem', backgroundColor: '#0c0c16', border: '1px solid #2a2a3a', borderRadius: '0.5rem', color: 'white' }}
              />
            </div>
            {loading && <div className="loading-spinner"><div className="spinner"></div></div>}
            {suggestions.length > 0 && (
              <div style={{ position: 'absolute', zIndex: 10, width: '100%', backgroundColor: '#1a1a2a', border: '1px solid #2a2a3a', borderRadius: '0.5rem', marginTop: '0.25rem' }}>
                {suggestions.map(p => (
                  <div key={p.id} onClick={() => selectPlayer(p)} style={{ padding: '0.75rem', cursor: 'pointer', borderBottom: '1px solid #2a2a3a' }}>
                    <div style={{ fontWeight: '500', color: 'white' }}>{cleanName(p.name)}</div>
                    <div style={{ fontSize: '0.875rem', color: '#b8baff' }}>{p.club}</div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    );
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#0c0c16', padding: '2rem' }}>
      {showWinnerAnim && winner && <WinnerConfetti />}
      
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h1 style={{ fontSize: '2rem', fontWeight: 'bold', color: 'white', marginBottom: '0.5rem' }}>Spieler-Vergleich</h1>
          <p style={{ color: '#b8baff' }}>Vergleiche zwei Spieler direkt miteinander</p>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '1.5rem', marginBottom: '2rem' }}>
          {renderPlayerCard(player1, 'Spieler 1', player1 ? removePlayer1 : null, true)}
          {renderPlayerCard(player2, 'Spieler 2', player2 ? removePlayer2 : null, false)}
        </div>

        {player1 && player2 && !showComparison && (
          <div style={{ textAlign: 'center' }}>
            <button onClick={startComparison} style={{ padding: '0.75rem 2rem', backgroundColor: '#6666ff', color: 'white', border: 'none', borderRadius: '0.5rem', cursor: 'pointer', fontSize: '1rem' }}>
              Vergleich starten
            </button>
          </div>
        )}

        {showComparison && (
          <div style={{ marginTop: '2rem' }}>
            {winner && (
              <div style={{
                background: 'linear-gradient(135deg, #b45309, #d97706)',
                borderRadius: '0.75rem',
                padding: '1rem',
                marginBottom: '1.5rem',
                textAlign: 'center',
                boxShadow: '0 4px 12px rgba(0,0,0,0.3)'
              }}>
                <Trophy className="w-8 h-8 mx-auto mb-2" style={{ color: '#fbbf24' }} />
                <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'white' }}>
                  Sieger: {cleanName(winner.name)}
                </div>
                <div style={{ fontSize: '0.875rem', color: '#fef3c7' }}>
                  {score1?.totalScore > score2?.totalScore ? 
                    `${score1.totalScore} : ${score2.totalScore} Punkte` : 
                    `${score2.totalScore} : ${score1.totalScore} Punkte`}
                </div>
              </div>
            )}

            <div style={{ backgroundColor: '#1a1a2a', borderRadius: '0.75rem', padding: '1.5rem', marginBottom: '1.5rem' }}>
              <h2 style={{ fontSize: '1.25rem', fontWeight: '600', color: 'white', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Target className="w-5 h-5" /> Fähigkeiten Radar
              </h2>
              <div style={{ height: '400px', width: '100%' }}>
                <ResponsiveContainer width="100%" height="100%">
                  <RadarChart data={radarData}>
                    <PolarGrid stroke="#2a2a3a" />
                    <PolarAngleAxis dataKey="category" tick={{ fill: '#b8baff', fontSize: 10 }} />
                    <Radar name={cleanName(player1?.name)} dataKey="player1" stroke="#6666ff" fill="#6666ff" fillOpacity={0.3} />
                    <Radar name={cleanName(player2?.name)} dataKey="player2" stroke="#b8baff" fill="#b8baff" fillOpacity={0.3} />
                    <Legend wrapperStyle={{ color: 'white' }} />
                  </RadarChart>
                </ResponsiveContainer>
              </div>
            </div>

            <div style={{ backgroundColor: '#1a1a2a', borderRadius: '0.75rem', overflow: 'hidden' }}>
              <h2 style={{ fontSize: '1.25rem', fontWeight: '600', color: 'white', padding: '1rem 1.5rem 0 1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Activity className="w-5 h-5" /> Detailvergleich
              </h2>
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr style={{ backgroundColor: '#0c0c16', borderBottom: '1px solid #2a2a3a' }}>
                      <th style={{ padding: '0.75rem 1rem', textAlign: 'left', color: '#b8baff' }}>Kategorie</th>
                      <th style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{player1 ? cleanName(player1.name) : 'Spieler 1'}</th>
                      <th style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{player2 ? cleanName(player2.name) : 'Spieler 2'}</th>
                      <th style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>Vergleich</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr style={{ borderBottom: '1px solid #2a2a3a' }}>
                      <td style={{ padding: '0.75rem 1rem', color: 'white', fontWeight: '500' }}>Alter</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{player1?.age || '?'} Jahre</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{player2?.age || '?'} Jahre</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>
                        {player1?.age < player2?.age ? '➡️ Jünger' : player2?.age < player1?.age ? '⬅️ Jünger' : '⚖️ Gleich'}
                      </td>
                    </tr>
                    <tr style={{ borderBottom: '1px solid #2a2a3a' }}>
                      <td style={{ padding: '0.75rem 1rem', color: 'white', fontWeight: '500' }}>Marktwert</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{formatMarketValue(marketValue1)}</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{formatMarketValue(marketValue2)}</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>
                        {parseFloat(marketValue1) > parseFloat(marketValue2) ? '➡️ Höher' : parseFloat(marketValue2) > parseFloat(marketValue1) ? '⬅️ Höher' : '⚖️ Gleich'}
                      </td>
                    </tr>
                    <tr style={{ borderBottom: '1px solid #2a2a3a' }}>
                      <td style={{ padding: '0.75rem 1rem', color: 'white', fontWeight: '500' }}>TransferScore</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{score1?.totalScore || '?'} / 100</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{score2?.totalScore || '?'} / 100</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>
                        {score1?.totalScore > score2?.totalScore ? '➡️ Besser' : score2?.totalScore > score1?.totalScore ? '⬅️ Besser' : '⚖️ Gleich'}
                      </td>
                    </tr>
                    <tr style={{ borderBottom: '1px solid #2a2a3a' }}>
                      <td style={{ padding: '0.75rem 1rem', color: 'white', fontWeight: '500' }}>Position</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{player1?.position?.split(' - ')[0] || '?'}</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{player2?.position?.split(' - ')[0] || '?'}</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>-</td>
                    </tr>
                    <tr style={{ borderBottom: '1px solid #2a2a3a' }}>
                      <td style={{ padding: '0.75rem 1rem', color: 'white', fontWeight: '500' }}>Nationalität</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{player1?.nationality || '?'}</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>{player2?.nationality || '?'}</td>
                      <td style={{ padding: '0.75rem 1rem', textAlign: 'center', color: '#b8baff' }}>-</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'center', marginTop: '1.5rem' }}>
              <button
                onClick={() => setShowComparison(false)}
                style={{ color: '#6666ff', background: 'none', border: 'none', cursor: 'pointer', padding: '0.5rem 1rem' }}
              >
                Neuen Vergleich starten
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default PlayerCompare;