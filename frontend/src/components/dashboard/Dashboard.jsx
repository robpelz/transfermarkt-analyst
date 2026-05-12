import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Users, Trophy, TrendingUp, ArrowRight, Star, Target, Award } from 'lucide-react';
import playerService from '../../services/playerService';
import StatCard from './StatCard';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalPlayers: 0,
    totalClubs: 0,
    totalTransfers: 0,
    averageScore: 0,
    topPlayers: [],
    topClubs: []
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const topPlayers = await playerService.getSuggestions();
      
      const topPlayersWithScore = topPlayers.map((player, index) => ({
        id: player.id,
        name: player.name,
        club: player.club,
        score: Math.floor(70 + Math.random() * 25),
        imageUrl: player.imageUrl
      }));
      
      setStats({
        totalPlayers: 92671,
        totalClubs: 4,
        totalTransfers: 456,
        averageScore: 72,
        topPlayers: topPlayersWithScore,
        topClubs: [
          { id: 1, name: 'German Bundesliga', score: 92 },
          { id: 2, name: 'Premier League', score: 94 },
          { id: 3, name: 'La Liga', score: 88 }
        ]
      });
    } catch (error) {
      console.error('Error loading dashboard:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-white text-xl">Loading dashboard...</div>
      </div>
    );
  }

  return (
    <div>
      {/* Welcome Section */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-white mb-2">
          Welcome back, <span className="text-[#6666ff]">Analyst</span>
        </h1>
        <p className="text-[#b8baff] text-lg">
          Here's what's happening with your transfer analysis
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <StatCard title="Total Players" value={stats.totalPlayers} icon={<Users className="w-6 h-6" />} color="blue" trend="+12 this month" />
        <StatCard title="Total Clubs" value={stats.totalClubs} icon={<Trophy className="w-6 h-6" />} color="purple" trend="+3 this month" />
        <StatCard title="Transfers" value={stats.totalTransfers} icon={<TrendingUp className="w-6 h-6" />} color="green" trend="+28 this month" />
        <StatCard title="Avg. Score" value={`${stats.averageScore}%`} icon={<Target className="w-6 h-6" />} color="yellow" trend="+5% vs last month" />
      </div>

      {/* Two Column Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Top Players */}
        <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold text-white">Top Players</h2>
            <Link to="/players" className="text-[#6666ff] hover:text-[#b8baff] text-sm flex items-center gap-1">
              View All <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
          <div className="space-y-4">
            {stats.topPlayers.map((player, index) => (
              <Link key={player.id} to={`/players/${player.id}`} className="block">
                <div className="flex items-center justify-between p-3 bg-[#0c0c16] rounded-lg hover:bg-[#2a2a3a] transition-colors">
                  <div className="flex items-center gap-3">
                    <span className="text-[#b8baff] font-medium">#{index + 1}</span>
                    <div>
                      <p className="text-white font-medium">{player.name}</p>
                      <p className="text-[#b8baff] text-sm">{player.club}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-[#6666ff] font-bold">{player.score}</span>
                    <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>

        {/* Top Clubs */}
        <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold text-white">Top Clubs</h2>
            <Link to="/clubs" className="text-[#6666ff] hover:text-[#b8baff] text-sm flex items-center gap-1">
              View All <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
          <div className="space-y-4">
            {stats.topClubs.map((club, index) => (
              <Link key={club.id} to="/clubs" className="block">
                <div className="flex items-center justify-between p-3 bg-[#0c0c16] rounded-lg hover:bg-[#2a2a3a] transition-colors">
                  <div className="flex items-center gap-3">
                    <span className="text-[#b8baff] font-medium">#{index + 1}</span>
                    <div>
                      <p className="text-white font-medium">{club.name}</p>
                      <p className="text-[#b8baff] text-sm">Success Rate</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-[#6666ff] font-bold">{club.score}%</span>
                    <Award className="w-4 h-4 text-[#6666ff]" />
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;