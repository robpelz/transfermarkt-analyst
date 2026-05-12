import api from './api';

const playerService = {
  searchPlayers: async (query) => {
    try {
      const response = await api.get(`/sofifa/full-search?query=${encodeURIComponent(query)}`, {
        timeout: 15000
      });
      
      const transformedData = response.data.map(player => ({
        id: player.PLAYER_ID,
        name: player.PLAYER_NAME,
        position: player.POSITION,
        nationality: player.CITIZENSHIP,
        club: player.CURRENT_CLUB_NAME,
        value: player.market_value,
        age: player.age,
        imageUrl: null
      }));
      
      return transformedData;
    } catch (error) {
      console.error('❌ Fehler:', error);
      return [];
    }
  },

  getSuggestions: async () => {
    const topPlayers = ['Wirtz', 'Musiala', 'Bellingham', 'Haaland', 'Kane'];
    const results = [];
    for (const name of topPlayers) {
      const players = await playerService.searchPlayers(name);
      if (players && players.length > 0) {
        results.push(players[0]);
      }
    }
    return results;
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