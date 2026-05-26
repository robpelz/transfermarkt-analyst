// playerService.js
import api from './api';

const playerService = {
  searchPlayers: async (query) => {
    try {
      const response = await api.get(`/sofifa/full-search?query=${encodeURIComponent(query)}`, {
        timeout: 30000
      });
      
      console.log('API Response:', response.data);
      
      return response.data.map(player => ({
        id: player.player_id,
        name: player.player_name,
        position: player.position,
        nationality: player.citizenship,
        club: player.current_club_name,
        value: player.market_value || player.wert || '?',
        age: player.age || '?',
        imageUrl: null
      }));
    } catch (error) {
      console.error('❌ Fehler:', error);
      return [];
    }
  },

  getRandomPlayers: async () => {
    const randomNames = ['Wirtz', 'Haaland', 'Musiala', 'Bellingham', 'Kane', 'Saka', 'Pedri', 'Mbappé'];
    const players = await Promise.all(
      randomNames.map(name => playerService.searchPlayers(name))
    );
    return players.filter(p => p.length > 0).map(p => p[0]).slice(0, 8);
  },

  getById: async (id) => {
    try {
      const response = await api.get(`/sofifa/player/${id}`);
      return response.data;
    } catch (error) {
      console.error('Fehler bei getById:', error);
      return null;
    }
  },

  getTransferScore: async (id) => {
    try {
      const response = await api.get(`/sofifa/player/${id}/score?club=Napoli`);
      return response.data;
    } catch (error) {
      console.error('Fehler bei getTransferScore:', error);
      return { totalScore: 75 };
    }
  }
};

export default playerService;