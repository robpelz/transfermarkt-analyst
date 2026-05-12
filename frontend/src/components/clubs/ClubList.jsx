import { useState, useEffect } from 'react';
import { Loader2 } from 'lucide-react';
import ClubCard from './ClubCard';

const ClubList = () => {
  const [leagues, setLeagues] = useState([]);
  const [teams, setTeams] = useState([]);
  const [selectedLeagueName, setSelectedLeagueName] = useState(null);
  const [loadingTeams, setLoadingTeams] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const topLeagues = [
      { id: 1, name: 'Premier League', country: 'England' },
      { id: 2, name: 'Bundesliga', country: 'Germany' },
      { id: 3, name: 'Serie A', country: 'Italy' },
      { id: 4, name: 'La Liga', country: 'Spain' }
    ];
    setLeagues(topLeagues);
    setSelectedLeagueName(topLeagues[0].name);
  }, []);

  useEffect(() => {
    if (!selectedLeagueName) return;
    
    setLoadingTeams(true);
    setError(null);
    
    fetch(`/api/live/teams/teams/by-league?league=${encodeURIComponent(selectedLeagueName)}`)
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then(data => {
        let normalizedTeams = [];
        if (Array.isArray(data)) {
          normalizedTeams = data.map(item => ({
            idTeam: item.id,
            strTeam: item.name,
            localLogoUrl: item.logo,
            strStadium: null,
            intFormedYear: null
          }));
        }
        setTeams(normalizedTeams);
        setLoadingTeams(false);
      })
      .catch(err => {
        console.error('Fehler beim Laden der Teams:', err);
        setError('Teams konnten nicht geladen werden');
        setLoadingTeams(false);
      });
  }, [selectedLeagueName]);

  if (error) return <div className="text-red-500 text-center py-12">{error}</div>;

  return (
    <div className="min-h-screen bg-[#0c0c16] py-8">
      <div className="container mx-auto px-4">
        <h1 className="text-3xl font-bold text-white mb-2">Vereine</h1>
        <p className="text-[#b8baff] mb-8">Top-Ligen und Teams</p>

        <div className="flex flex-wrap gap-3 mb-8">
          {leagues.map(league => (
            <button
              key={league.id}
              onClick={() => setSelectedLeagueName(league.name)}
              className={`px-4 py-2 rounded-lg transition-all ${
                selectedLeagueName === league.name
                  ? 'bg-[#6666ff] text-white'
                  : 'bg-[#1a1a2a] text-[#b8baff] hover:bg-[#2a2a3a]'
              }`}
            >
              {league.name}
            </button>
          ))}
        </div>

        {loadingTeams ? (
          <div className="flex justify-center py-12">
            <Loader2 className="w-8 h-8 text-[#6666ff] animate-spin" />
          </div>
        ) : teams.length === 0 ? (
          <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-12 text-center">
            <p className="text-[#b8baff] text-lg">Keine Teams gefunden</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {teams.map(team => (
              <ClubCard key={team.idTeam} club={team} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ClubList;