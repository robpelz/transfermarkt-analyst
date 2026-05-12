import { useState, useEffect } from 'react';
import { Star, Trash2, Edit2, Plus, X, Check } from 'lucide-react';
import axios from 'axios';
import playerService from '../../services/playerService';

const ScoutingList = () => {
  const [scoutingList, setScoutingList] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedPlayer, setSelectedPlayer] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [rating, setRating] = useState(3);
  const [note, setNote] = useState('');
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    loadScoutingList();
  }, []);

  const loadScoutingList = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/scouting');
      setScoutingList(response.data);
    } catch (error) {
      console.error('Fehler beim Laden:', error);
    } finally {
      setLoading(false);
    }
  };

  const searchPlayers = async (query) => {
    if (!query.trim()) {
      setSearchResults([]);
      return;
    }
    try {
      const results = await playerService.searchPlayers(query);
      setSearchResults(results.slice(0, 5));
    } catch (error) {
      console.error('Fehler bei Suche:', error);
    }
  };

  const addScouting = async () => {
    if (!selectedPlayer) return;
    try {
      await axios.post(`http://localhost:8080/api/scouting/${selectedPlayer.id}`, null, {
        params: { playerName: selectedPlayer.name, rating, note }
      });
      setShowModal(false);
      resetModal();
      loadScoutingList();
    } catch (error) {
      console.error('Fehler beim Hinzufügen:', error);
    }
  };

  const updateScouting = async () => {
    if (!editingId) return;
    try {
      await axios.put(`http://localhost:8080/api/scouting/${editingId}`, null, {
        params: { rating, note }
      });
      setEditingId(null);
      resetModal();
      loadScoutingList();
    } catch (error) {
      console.error('Fehler beim Aktualisieren:', error);
    }
  };

  const deleteScouting = async (id) => {
    if (window.confirm('Wirklich löschen?')) {
      try {
        await axios.delete(`http://localhost:8080/api/scouting/${id}`);
        loadScoutingList();
      } catch (error) {
        console.error('Fehler beim Löschen:', error);
      }
    }
  };

  const openEditModal = (item) => {
    setEditingId(item.id);
    setSelectedPlayer({ id: item.playerId, name: item.playerName });
    setRating(item.rating);
    setNote(item.note || '');
    setShowModal(true);
  };

  const resetModal = () => {
    setSelectedPlayer(null);
    setSearchQuery('');
    setSearchResults([]);
    setRating(3);
    setNote('');
    setEditingId(null);
  };

  const handleSearch = (value) => {
    setSearchQuery(value);
    searchPlayers(value);
  };

  const StarRating = ({ value, onChange }) => (
    <div className="flex gap-1">
      {[1, 2, 3, 4, 5].map((star) => (
        <button
          key={star}
          type="button"
          onClick={() => onChange(star)}
          className="focus:outline-none"
        >
          <Star
            className={`w-8 h-8 ${star <= value ? 'fill-yellow-500 text-yellow-500' : 'text-[#2a2a3a]'}`}
          />
        </button>
      ))}
    </div>
  );

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0c0c16] flex items-center justify-center">
        <div className="text-white">Lade Scouting-Liste...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0c0c16] py-8">
      <div className="container mx-auto px-4 max-w-4xl">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-white">📋 Meine Scout-Liste</h1>
          <button
            onClick={() => setShowModal(true)}
            className="bg-[#6666ff] hover:bg-[#b8baff] text-white px-4 py-2 rounded-lg transition-colors flex items-center gap-2"
          >
            <Plus className="w-5 h-5" />
            Spieler hinzufügen
          </button>
        </div>

        {scoutingList.length === 0 ? (
          <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-12 text-center">
            <p className="text-[#b8baff] text-lg">Keine Spieler in der Scout-Liste</p>
            <button
              onClick={() => setShowModal(true)}
              className="mt-4 text-[#6666ff] hover:text-[#b8baff]"
            >
              + Ersten Spieler hinzufügen
            </button>
          </div>
        ) : (
          <div className="space-y-3">
            {scoutingList.map((item) => (
              <div
                key={item.id}
                className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-4 hover:border-[#6666ff] transition-all"
              >
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-gradient-to-br from-[#6666ff] to-[#b8baff] flex items-center justify-center">
                        <span className="text-white font-bold">
                          {item.playerName?.charAt(0) || '?'}
                        </span>
                      </div>
                      <div>
                        <h3 className="font-semibold text-white">{item.playerName}</h3>
                        <div className="flex gap-2 text-sm text-[#b8baff]">
                          <div className="flex gap-0.5">
                            {[1, 2, 3, 4, 5].map((star) => (
                              <Star
                                key={star}
                                className={`w-4 h-4 ${star <= item.rating ? 'fill-yellow-500 text-yellow-500' : 'text-[#2a2a3a]'}`}
                              />
                            ))}
                          </div>
                          {item.note && <span>• {item.note}</span>}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={() => openEditModal(item)}
                      className="p-2 hover:bg-[#2a2a3a] rounded-lg transition-colors"
                    >
                      <Edit2 className="w-5 h-5 text-[#b8baff]" />
                    </button>
                    <button
                      onClick={() => deleteScouting(item.id)}
                      className="p-2 hover:bg-[#2a2a3a] rounded-lg transition-colors"
                    >
                      <Trash2 className="w-5 h-5 text-red-500" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Modal für Hinzufügen/Bearbeiten */}
        {showModal && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-6 max-w-md w-full">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-semibold text-white">
                  {editingId ? 'Bewertung bearbeiten' : 'Spieler hinzufügen'}
                </h2>
                <button
                  onClick={() => {
                    setShowModal(false);
                    resetModal();
                  }}
                  className="text-[#b8baff] hover:text-white"
                >
                  <X className="w-6 h-6" />
                </button>
              </div>

              {!selectedPlayer && !editingId && (
                <div className="mb-4">
                  <input
                    type="text"
                    placeholder="Spieler suchen..."
                    value={searchQuery}
                    onChange={(e) => handleSearch(e.target.value)}
                    className="w-full px-4 py-2 bg-[#0c0c16] border border-[#2a2a3a] rounded-lg text-white placeholder-[#b8baff] focus:outline-none focus:border-[#6666ff]"
                  />
                  {searchResults.length > 0 && (
                    <div className="mt-2 bg-[#0c0c16] border border-[#2a2a3a] rounded-lg overflow-hidden">
                      {searchResults.map((player) => (
                        <div
                          key={player.id}
                          onClick={() => setSelectedPlayer(player)}
                          className="p-3 hover:bg-[#2a2a3a] cursor-pointer border-b border-[#2a2a3a] last:border-0"
                        >
                          <div className="font-medium text-white">{player.name}</div>
                          <div className="text-sm text-[#b8baff]">{player.club}</div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}

              {selectedPlayer && (
                <div className="mb-4 p-3 bg-[#0c0c16] rounded-lg">
                  <div className="font-medium text-white">{selectedPlayer.name}</div>
                  <div className="text-sm text-[#b8baff]">wird hinzugefügt</div>
                </div>
              )}

              <div className="mb-4">
                <label className="block text-[#b8baff] mb-2">Bewertung (1-5 Sterne)</label>
                <StarRating value={rating} onChange={setRating} />
              </div>

              <div className="mb-6">
                <label className="block text-[#b8baff] mb-2">Notiz (optional)</label>
                <textarea
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  rows="3"
                  className="w-full px-4 py-2 bg-[#0c0c16] border border-[#2a2a3a] rounded-lg text-white placeholder-[#b8baff] focus:outline-none focus:border-[#6666ff]"
                  placeholder="z.B. 'Gutes Talent für die Zukunft'"
                />
              </div>

              <div className="flex gap-3">
                <button
                  onClick={() => {
                    setShowModal(false);
                    resetModal();
                  }}
                  className="flex-1 px-4 py-2 bg-[#2a2a3a] hover:bg-[#3a3a4a] text-white rounded-lg transition-colors"
                >
                  Abbrechen
                </button>
                <button
                  onClick={editingId ? updateScouting : addScouting}
                  disabled={!editingId && !selectedPlayer}
                  className="flex-1 px-4 py-2 bg-[#6666ff] hover:bg-[#b8baff] text-white rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {editingId ? 'Speichern' : 'Hinzufügen'}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ScoutingList;