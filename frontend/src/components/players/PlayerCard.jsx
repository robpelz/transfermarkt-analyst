import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';

const PlayerCard = ({ player }) => {
  const [imageUrl, setImageUrl] = useState(null);

  useEffect(() => {
    if (!imageUrl && player.name) {
      const transfermarktUrl = `https://tmssl.akamaized.net/images/portrait/header/${player.id}.png`;
      setImageUrl(transfermarktUrl);
    }
  }, [player.name, player.id, imageUrl]);

  const formatMarketValue = (value) => {
    if (!value || value === '?') return '?';
    let num = String(value).replace(' €', '').replace(/\./g, '');
    const million = parseFloat(num) / 1000000;
    if (million >= 1000) return `${(million / 1000).toFixed(1)} Mrd`;
    if (million >= 1) return `${Math.round(million)} M`;
    return '?';
  };

  const cleanName = (player.name || 'Unbekannt').replace(/ \(\d+\)/, '');

  const displayPlayer = {
    id: player.id,
    name: cleanName,
    position: player.position || 'N/A',
    club: player.club || 'Vereinslos',
    age: player.age || '?',
    marketValue: formatMarketValue(player.value),
    nationality: player.nationality || 'Unbekannt',
    score: player.score || 75,
  };

  const getScoreClass = (score) => {
    if (score >= 85) return 'score-high';
    if (score >= 70) return 'score-mid';
    if (score >= 55) return 'score-low';
    return 'score-very-low';
  };

  return (
    <Link to={`/players/${displayPlayer.id}`} className="player-card">
      <div className="player-header">
        <div className="player-avatar">
          <img 
            src={imageUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(displayPlayer.name)}&background=6666ff&color=fff&size=48`}
            alt={displayPlayer.name}
            onError={(e) => {
              e.target.onerror = null;
              e.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(displayPlayer.name)}&background=6666ff&color=fff&size=48`;
            }}
          />
        </div>
        <div className="player-info">
          <div className="player-name">{displayPlayer.name}</div>
          <div className="player-meta">
            <span className="player-badge">{displayPlayer.position}</span>
            <span>•</span>
            <span>{displayPlayer.club}</span>
          </div>
        </div>
        <div className={`player-score ${getScoreClass(displayPlayer.score)}`}>
          {displayPlayer.score}
        </div>
      </div>

      <div className="player-stats">
        <div className="player-stat-item">
          <div className="player-stat-icon">📅</div>
          <div className="player-stat-value">{displayPlayer.age}</div>
          <div className="player-stat-label">Alter</div>
        </div>
        <div className="player-stat-item">
          <div className="player-stat-icon">💰</div>
          <div className="player-stat-value">{displayPlayer.marketValue}</div>
          <div className="player-stat-label">Marktwert</div>
        </div>
        <div className="player-stat-item">
          <div className="player-stat-icon">🌍</div>
          <div className="player-stat-value">{displayPlayer.nationality}</div>
          <div className="player-stat-label">Nationalität</div>
        </div>
      </div>

      <div className="player-footer">
        <div className="player-trend">
          <span>📈</span>
          <span>Stabil</span>
        </div>
        <span className="player-detail-link">Details →</span>
      </div>
    </Link>
  );
};

export default PlayerCard;