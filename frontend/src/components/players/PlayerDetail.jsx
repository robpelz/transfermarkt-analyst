import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Calendar, MapPin, DollarSign, TrendingUp, ArrowLeft } from 'lucide-react';
import { RadarChart, PolarGrid, PolarAngleAxis, Radar, ResponsiveContainer } from 'recharts';
import axios from 'axios';
import playerService from '../../services/playerService';

const PlayerDetail = () => {
  const { id } = useParams();
  const [player, setPlayer] = useState(null);
  const [score, setScore] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadPlayer = async () => {
      if (!id) {
        setError('Keine Spieler-ID angegeben');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const playerData = await playerService.getById(id);
        
        if (!playerData) {
          setError('Spieler nicht gefunden');
          setLoading(false);
          return;
        }
        
        setPlayer(playerData);
        
        try {
          const scoreData = await playerService.getTransferScore(id);
          setScore(scoreData);
        } catch (scoreErr) {
          setScore(calculateFallbackScore(playerData));
        }
        
      } catch (err) {
        console.error('Fehler:', err);
        setError('Spieler nicht gefunden');
      } finally {
        setLoading(false);
      }
    };

    loadPlayer();
  }, [id]);

  const calculateFallbackScore = (playerData) => {
    let totalScore = 50;
    if (playerData.age && playerData.age > 0) {
      if (playerData.age < 23) totalScore += 25;
      else if (playerData.age < 28) totalScore += 15;
      else if (playerData.age > 32) totalScore -= 15;
    }
    totalScore = Math.min(100, Math.max(0, totalScore));
    return {
      totalScore: totalScore,
      positionScore: Math.min(100, totalScore + 5),
      priceScore: Math.min(100, totalScore),
      ageScore: Math.min(100, totalScore + 10),
      experienceScore: Math.min(100, totalScore - 5),
      competitionScore: Math.min(100, totalScore),
      recommendation: getRecommendation(totalScore)
    };
  };

  const getRecommendation = (score) => {
    if (score >= 80) return '🔥 Top-Transfer – sofort zuschlagen!';
    if (score >= 65) return '✅ Gutes Investment – empfehlenswert';
    if (score >= 50) return '⚠️ Solide, aber kein Schnäppchen';
    if (score >= 35) return '❌ Zu riskant – lieber nicht';
    return '🚫 Keine Empfehlung – Abstand halten';
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0c0c16] flex items-center justify-center">
        <div className="text-white text-xl">Lade Spieler-Daten...</div>
      </div>
    );
  }
  
  if (error || !player) {
    return (
      <div className="min-h-screen bg-[#0c0c16] py-8">
        <div className="container mx-auto px-4 text-center">
          <div className="text-red-500 text-xl mb-4">{error || 'Spieler nicht gefunden'}</div>
          <Link to="/players" className="text-[#6666ff] hover:underline">
            ← Zurück zur Suche
          </Link>
        </div>
      </div>
    );
  }

  const radarData = score ? [
    { category: 'Position', value: score.positionScore || 0 },
    { category: 'Preis', value: score.priceScore || 0 },
    { category: 'Alter', value: score.ageScore || 0 },
    { category: 'Erfahrung', value: score.experienceScore || 0 },
    { category: 'Konkurrenz', value: score.competitionScore || 0 },
  ] : [];

  return (
    <div className="min-h-screen bg-[#0c0c16] py-8">
      <div className="container mx-auto px-4 max-w-5xl">
        <Link to="/players" className="inline-flex items-center gap-2 text-[#b8baff] hover:text-[#6666ff] mb-6">
          <ArrowLeft className="w-4 h-4" />
          Zurück zur Spieler-Liste
        </Link>

        <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-6">
          <div className="flex flex-col md:flex-row gap-8">
            <div className="flex-shrink-0">
              <div className="w-48 h-48 rounded-full bg-gradient-to-br from-[#6666ff] to-[#b8baff] overflow-hidden">
                {player.imageUrl ? (
                  <img src={player.imageUrl} alt={player.name} className="w-full h-full object-cover" />
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-6xl">⚽</div>
                )}
              </div>
            </div>

            <div className="flex-1">
              <h1 className="text-3xl font-bold text-white mb-2">{player.name}</h1>
              <div className="flex flex-wrap gap-3 mb-6">
                <span className="bg-[#0c0c16] px-3 py-1 rounded-full text-[#b8baff] text-sm">
                  {player.positions?.[0] || player.position || 'N/A'}
                </span>
                <span className="bg-[#0c0c16] px-3 py-1 rounded-full text-[#b8baff] text-sm">
                  {player.club || 'Vereinslos'}
                </span>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                <StatItem icon={<Calendar />} label="Alter" value={player.age || '?'} />
                <StatItem icon={<MapPin />} label="Nationalität" value={player.nationality || '?'} />
                <StatItem icon={<DollarSign />} label="Marktwert" value={player.value || '?'} />
                <StatItem icon={<TrendingUp />} label="Score" value={score?.totalScore ? `${score.totalScore}/100` : '?'} />
              </div>

              {score && (
                <div className="bg-[#0c0c16] rounded-lg p-4 mb-6">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-[#b8baff]">TransferScore</span>
                    <span className="text-2xl font-bold text-[#6666ff]">{score.totalScore}</span>
                  </div>
                  <div className="w-full bg-[#2a2a3a] rounded-full h-2">
                    <div className="bg-[#6666ff] h-2 rounded-full" style={{ width: `${score.totalScore}%` }} />
                  </div>
                  <p className="text-sm text-[#b8baff] mt-2">{score.recommendation}</p>
                </div>
              )}
            </div>
          </div>

          {score && radarData.length > 0 && (
            <div className="mt-8 pt-6 border-t border-[#2a2a3a]">
              <h2 className="text-xl font-semibold text-white mb-4">Analyse nach Kriterien</h2>
              <div className="h-80 w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <RadarChart data={radarData}>
                    <PolarGrid stroke="#2a2a3a" />
                    <PolarAngleAxis dataKey="category" tick={{ fill: '#b8baff' }} />
                    <Radar name="Score" dataKey="value" stroke="#6666ff" fill="#6666ff" fillOpacity={0.5} />
                  </RadarChart>
                </ResponsiveContainer>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

const StatItem = ({ icon, label, value }) => (
  <div className="bg-[#0c0c16] rounded-lg p-3 text-center">
    <div className="text-[#6666ff] flex justify-center mb-1">{icon}</div>
    <div className="text-white font-semibold">{value}</div>
    <div className="text-xs text-[#b8baff]">{label}</div>
  </div>
);

export default PlayerDetail;