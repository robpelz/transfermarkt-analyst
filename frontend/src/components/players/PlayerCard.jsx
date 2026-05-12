import { Link } from 'react-router-dom';
import { MapPin, DollarSign, Calendar, TrendingUp, TrendingDown, Minus } from 'lucide-react';
import { useState, useEffect } from 'react';

const PlayerCard = ({ player }) => {
  const [imageUrl, setImageUrl] = useState(player.imageUrl);

  useEffect(() => {
    if (!imageUrl && player.name) {
      const transfermarktUrl = `https://tmssl.akamaized.net/images/portrait/header/${player.id}.png`;
      setImageUrl(transfermarktUrl);
    }
  }, [player.name, player.id, imageUrl]);

  const getScoreColor = (score) => {
    if (score >= 85) return 'bg-[#b9f0d7] text-[#0c0c16]';
    if (score >= 70) return 'bg-[#c9e8ff] text-[#0c0c16]';
    if (score >= 55) return 'bg-[#b8baff] text-[#0c0c16]';
    return 'bg-[#6666ff] text-white';
  };

  const getTrendIcon = (trend) => {
    if (trend === 'up') return <TrendingUp className="w-4 h-4 text-[#b9f0d7]" />;
    if (trend === 'down') return <TrendingDown className="w-4 h-4 text-[#6666ff]" />;
    return <Minus className="w-4 h-4 text-[#b8baff]" />;
  };

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
    trend: player.trend || 'stable'
  };

  return (
    <Link to={`/players/${displayPlayer.id}`}>
      <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-5 hover:border-[#6666ff] transition-all group cursor-pointer">
        <div className="flex items-start gap-3 mb-3">
          <div className="w-12 h-12 rounded-full bg-gradient-to-br from-[#6666ff] to-[#b8baff] flex-shrink-0 overflow-hidden">
            <img 
              src={imageUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(displayPlayer.name)}&background=6666ff&color=fff&size=48`}
              alt={displayPlayer.name}
              className="w-full h-full object-cover"
              onError={(e) => {
                e.target.onerror = null;
                e.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(displayPlayer.name)}&background=6666ff&color=fff&size=48`;
              }}
            />
          </div>
          
          <div className="flex-1">
            <h3 className="font-semibold text-lg text-white group-hover:text-[#6666ff] transition-colors">
              {displayPlayer.name}
            </h3>
            <div className="flex items-center gap-2 text-sm text-[#b8baff]">
              <span className="bg-[#0c0c16] px-2 py-0.5 rounded">
                {displayPlayer.position}
              </span>
              <span>•</span>
              <span>{displayPlayer.club}</span>
            </div>
          </div>
          
          <div className={`px-3 py-1 rounded-full text-sm font-medium ${getScoreColor(displayPlayer.score)}`}>
            {displayPlayer.score}
          </div>
        </div>

        <div className="grid grid-cols-3 gap-2 mb-4">
          <div className="bg-[#0c0c16] rounded-lg p-2 text-center">
            <Calendar className="w-4 h-4 text-[#6666ff] mx-auto mb-1" />
            <div className="text-white text-sm font-medium">{displayPlayer.age}</div>
            <div className="text-xs text-[#b8baff]">Alter</div>
          </div>
          
          <div className="bg-[#0c0c16] rounded-lg p-2 text-center">
            <DollarSign className="w-4 h-4 text-[#6666ff] mx-auto mb-1" />
            <div className="text-white text-sm font-medium">{displayPlayer.marketValue}</div>
            <div className="text-xs text-[#b8baff]">Marktwert</div>
          </div>
          
          <div className="bg-[#0c0c16] rounded-lg p-2 text-center">
            <MapPin className="w-4 h-4 text-[#6666ff] mx-auto mb-1" />
            <div className="text-white text-sm font-medium truncate">
              {displayPlayer.nationality}
            </div>
            <div className="text-xs text-[#b8baff]">Nationalität</div>
          </div>
        </div>

        <div className="flex items-center justify-between pt-2 border-t border-[#2a2a3a]">
          <div className="flex items-center gap-2">
            {getTrendIcon(displayPlayer.trend)}
            <span className="text-sm text-[#b8baff]">
              {displayPlayer.trend === 'up' ? '+5% diesen Monat' : 
               displayPlayer.trend === 'down' ? '-3% diesen Monat' : 
               'Stabil'}
            </span>
          </div>
          <span className="text-sm text-[#6666ff] group-hover:text-[#b8baff] transition-colors">
            Details →
          </span>
        </div>
      </div>
    </Link>
  );
};

export default PlayerCard;