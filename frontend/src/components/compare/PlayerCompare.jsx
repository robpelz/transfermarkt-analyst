import { useState } from 'react';
import { Search, Loader2, TrendingUp, Trophy, X, Star, Calendar, DollarSign, MapPin } from 'lucide-react';
import playerService from '../../services/playerService';

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

  const searchPlayer = async (query, setSuggestions, setLoading) => {
    if (!query.trim()) {
      setSuggestions([]);
      return;
    }
    setLoading(true);
    try {
      const results = await playerService.searchPlayers(query);
      setSuggestions(results.slice(0, 5));
    } catch (error) {
      console.error('Fehler:', error);
    } finally {
      setLoading(false);
    }
  };

  const selectPlayer = (player, setPlayer, setSuggestions, setSearch) => {
    setPlayer(player);
    setSuggestions([]);
    setSearch(player.name);
  };

  const removePlayer = (setPlayer, setSearch) => {
    setPlayer(null);
    setSearch('');
  };

  const startComparison = () => {
    if (player1 && player2) {
      setShowComparison(true);
    }
  };

  const renderPlayerCard = (player, title, onRemove) => (
    <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-6">
      <div className="flex justify-between items-start mb-4">
        <h3 className="text-lg font-semibold text-white">{title}</h3>
        {onRemove && (
          <button onClick={onRemove} className="text-[#b8baff] hover:text-red-500">
            <X className="w-5 h-5" />
          </button>
        )}
      </div>
      {player ? (
        <div>
          <div className="flex items-center gap-3 mb-4">
            <div className="w-16 h-16 rounded-full bg-gradient-to-br from-[#6666ff] to-[#b8baff] flex items-center justify-center overflow-hidden">
              <img 
                src={`https://ui-avatars.com/api/?name=${encodeURIComponent(player.name)}&background=6666ff&color=fff&size=64`}
                alt={player.name}
                className="w-full h-full object-cover"
              />
            </div>
            <div>
              <h4 className="text-xl font-bold text-white">{player.name}</h4>
              <p className="text-[#b8baff]">{player.club}</p>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="bg-[#0c0c16] rounded-lg p-2 text-center">
              <Calendar className="w-4 h-4 text-[#6666ff] mx-auto mb-1" />
              <div className="text-white font-semibold">{player.age || '?'}</div>
              <div className="text-xs text-[#b8baff]">Alter</div>
            </div>
            <div className="bg-[#0c0c16] rounded-lg p-2 text-center">
              <DollarSign className="w-4 h-4 text-[#6666ff] mx-auto mb-1" />
              <div className="text-white font-semibold">{player.value || '?'}</div>
              <div className="text-xs text-[#b8baff]">Marktwert</div>
            </div>
            <div className="bg-[#0c0c16] rounded-lg p-2 text-center">
              <MapPin className="w-4 h-4 text-[#6666ff] mx-auto mb-1" />
              <div className="text-white font-semibold truncate">{player.nationality || '?'}</div>
              <div className="text-xs text-[#b8baff]">Nationalität</div>
            </div>
            <div className="bg-[#0c0c16] rounded-lg p-2 text-center">
              <TrendingUp className="w-4 h-4 text-[#6666ff] mx-auto mb-1" />
              <div className="text-white font-semibold">{player.position || '?'}</div>
              <div className="text-xs text-[#b8baff]">Position</div>
            </div>
          </div>
        </div>
      ) : (
        <div>
          <div className="relative">
            <div className="flex gap-2 mb-3">
              <input
                type="text"
                placeholder="Spieler suchen..."
                value={title === 'Spieler 1' ? search1 : search2}
                onChange={(e) => {
                  if (title === 'Spieler 1') {
                    setSearch1(e.target.value);
                    searchPlayer(e.target.value, setSuggestions1, setLoading1);
                  } else {
                    setSearch2(e.target.value);
                    searchPlayer(e.target.value, setSuggestions2, setLoading2);
                  }
                }}
                className="flex-1 px-4 py-2 bg-[#0c0c16] border border-[#2a2a3a] rounded-lg text-white placeholder-[#b8baff] focus:outline-none focus:border-[#6666ff]"
              />
            </div>
            {(title === 'Spieler 1' ? loading1 : loading2) && (
              <div className="flex justify-center py-4">
                <Loader2 className="w-5 h-5 text-[#6666ff] animate-spin" />
              </div>
            )}
            {(title === 'Spieler 1' ? suggestions1 : suggestions2).length > 0 && (
              <div className="absolute z-10 w-full bg-[#1a1a2a] border border-[#2a2a3a] rounded-lg mt-1">
                {(title === 'Spieler 1' ? suggestions1 : suggestions2).map(p => (
                  <div
                    key={p.id}
                    onClick={() => {
                      if (title === 'Spieler 1') {
                        selectPlayer(p, setPlayer1, setSuggestions1, setSearch1);
                      } else {
                        selectPlayer(p, setPlayer2, setSuggestions2, setSearch2);
                      }
                    }}
                    className="p-3 hover:bg-[#2a2a3a] cursor-pointer border-b border-[#2a2a3a] last:border-0"
                  >
                    <div className="font-medium text-white">{p.name}</div>
                    <div className="text-sm text-[#b8baff]">{p.club}</div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );

  const getComparisonResult = () => {
    if (!player1 || !player2) return null;
    
    const value1 = parseFloat(player1.value) || 0;
    const value2 = parseFloat(player2.value) || 0;
    const age1 = player1.age || 0;
    const age2 = player2.age || 0;
    
    let winner = null;
    let reason = '';
    
    if (value1 > value2 && age1 < age2) {
      winner = player1;
      reason = 'Höherer Marktwert bei geringerem Alter';
    } else if (value2 > value1 && age2 < age1) {
      winner = player2;
      reason = 'Höherer Marktwert bei geringerem Alter';
    } else if (value1 > value2) {
      winner = player1;
      reason = 'Höherer Marktwert';
    } else if (value2 > value1) {
      winner = player2;
      reason = 'Höherer Marktwert';
    } else if (age1 < age2) {
      winner = player1;
      reason = 'Geringeres Alter (mehr Potential)';
    } else if (age2 < age1) {
      winner = player2;
      reason = 'Geringeres Alter (mehr Potential)';
    } else {
      winner = player1;
      reason = 'Ähnliche Werte';
    }
    
    return { winner, reason };
  };

  const comparison = showComparison ? getComparisonResult() : null;

  return (
    <div className="min-h-screen bg-[#0c0c16] py-8">
      <div className="container mx-auto px-4 max-w-6xl">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-white mb-2">Spieler-Vergleich</h1>
          <p className="text-[#b8baff]">Vergleiche zwei Spieler direkt miteinander</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {renderPlayerCard(player1, 'Spieler 1', player1 ? () => removePlayer(setPlayer1, setSearch1) : null)}
          {renderPlayerCard(player2, 'Spieler 2', player2 ? () => removePlayer(setPlayer2, setSearch2) : null)}
        </div>

        {player1 && player2 && !showComparison && (
          <div className="text-center">
            <button
              onClick={startComparison}
              className="bg-[#6666ff] hover:bg-[#b8baff] text-white px-8 py-3 rounded-lg transition-colors font-semibold flex items-center gap-2 mx-auto"
            >
              <Trophy className="w-5 h-5" />
              Vergleich starten
            </button>
          </div>
        )}

        {showComparison && comparison && (
          <div className="mt-8">
            <div className="bg-gradient-to-r from-[#1a1a2a] to-[#2a2a3a] rounded-xl p-6">
              <h2 className="text-xl font-semibold text-white mb-4 text-center">Vergleichsergebnis</h2>
              
              <div className="grid grid-cols-3 gap-4 mb-6">
                <div className="text-center">
                  <p className="text-sm text-[#b8baff] mb-2">{player1.name}</p>
                  <div className="text-2xl font-bold text-white">{player1.age}</div>
                  <p className="text-xs text-[#b8baff]">Alter</p>
                </div>
                <div className="text-center border-x border-[#2a2a3a]">
                  <p className="text-sm text-[#b8baff] mb-2">⚖️ Direkter Vergleich ⚖️</p>
                  <div className="text-2xl font-bold text-[#6666ff]">VS</div>
                </div>
                <div className="text-center">
                  <p className="text-sm text-[#b8baff] mb-2">{player2.name}</p>
                  <div className="text-2xl font-bold text-white">{player2.age}</div>
                  <p className="text-xs text-[#b8baff]">Alter</p>
                </div>
              </div>

              <div className="bg-[#0c0c16] rounded-lg p-6 text-center">
                <Trophy className="w-12 h-12 text-yellow-500 mx-auto mb-3" />
                <h3 className="text-xl font-bold text-white mb-2">
                  {comparison.winner.name} ist die bessere Wahl!
                </h3>
                <p className="text-[#b8baff]">{comparison.reason}</p>
              </div>

              <div className="flex justify-center mt-6">
                <button
                  onClick={() => {
                    setShowComparison(false);
                    setPlayer1(null);
                    setPlayer2(null);
                    setSearch1('');
                    setSearch2('');
                  }}
                  className="text-[#6666ff] hover:text-[#b8baff] transition-colors"
                >
                  Neue Vergleich starten
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default PlayerCompare;