import { useState, useEffect } from 'react';
import { Search, Loader2, Flame } from 'lucide-react';
import PlayerCard from './PlayerCard';
import playerService from '../../services/playerService';

const PlayerList = () => {
  const [query, setQuery] = useState('');
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searched, setSearched] = useState(false);
  const [suggestions, setSuggestions] = useState([]);
  const [loadingSuggestions, setLoadingSuggestions] = useState(true);

  useEffect(() => {
    loadSuggestions();
  }, []);

  const loadSuggestions = async () => {
    setLoadingSuggestions(true);
    try {
      const topPlayers = ['Wirtz', 'Musiala', 'Bellingham', 'Haaland', 'Kane'];
      const suggestionsData = await Promise.all(
        topPlayers.map(async (name) => {
          const players = await playerService.searchPlayers(name);
          if (players && players.length > 0) {
            const player = players[0];
            return { ...player, score: 75 };
          }
          return null;
        })
      );
      setSuggestions(suggestionsData.filter(p => p !== null));
    } catch (err) {
      console.error('Fehler:', err);
    } finally {
      setLoadingSuggestions(false);
    }
  };

  const searchPlayers = async () => {
    if (!query.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const data = await playerService.searchPlayers(query);
      setPlayers(data);
      setSearched(true);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#0c0c16] py-8">
      <div className="container mx-auto px-4">
        <h1 className="text-3xl font-bold text-white mb-2">Spieler-Suche</h1>
        <p className="text-[#b8baff] mb-8">Suche nach Spielern in der Datenbank</p>

        <div className="flex gap-3 mb-8">
          <input
            type="text"
            placeholder="Spieler suchen (z.B. Wirtz, Haaland, Mbappé)"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && searchPlayers()}
            className="flex-1 px-4 py-3 bg-[#1a1a2a] border border-[#2a2a3a] rounded-lg text-white placeholder-[#b8baff] focus:outline-none focus:border-[#6666ff]"
          />
          <button
            onClick={searchPlayers}
            disabled={loading}
            className="bg-[#6666ff] hover:bg-[#b8baff] text-white px-6 py-3 rounded-lg transition-colors disabled:opacity-50"
          >
            {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Suchen'}
          </button>
        </div>

        {!searched && (
          <div className="mb-8">
            <div className="flex items-center gap-2 mb-4">
              <Flame className="w-5 h-5 text-orange-500" />
              <h2 className="text-xl font-semibold text-white">Heiße Spieler 🔥</h2>
            </div>
            {loadingSuggestions ? (
              <div className="flex justify-center py-12">
                <Loader2 className="w-8 h-8 text-[#6666ff] animate-spin" />
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {suggestions.slice(0, 6).map(player => (
                  <PlayerCard key={player.id} player={player} />
                ))}
              </div>
            )}
          </div>
        )}

        {error && (
          <div className="bg-red-500/10 border border-red-500 text-red-500 p-4 rounded-lg mb-8">
            Fehler: {error}
          </div>
        )}

        {searched && !loading && (
          <>
            {players.length === 0 ? (
              <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-12 text-center">
                <p className="text-[#b8baff] text-lg">Keine Spieler gefunden</p>
              </div>
            ) : (
              <>
                <p className="text-[#b8baff] mb-4">{players.length} Spieler gefunden</p>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {players.map(player => (
                    <PlayerCard key={player.id} player={player} />
                  ))}
                </div>
              </>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default PlayerList;