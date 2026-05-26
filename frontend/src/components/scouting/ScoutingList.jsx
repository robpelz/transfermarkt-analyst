import { useState, useEffect } from 'react';
import axios from 'axios';
import { Star, Trash2, Edit2, Plus, X } from 'lucide-react';
import playerService from '../../services/playerService';

const StarRating = ({ value, onChange }) => (
  <div style={{ display: 'flex', gap: '0.25rem', marginBottom: '1rem' }}>
    {[1, 2, 3, 4, 5].map((star) => (
      <button key={star} onClick={() => onChange(star)} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '1.5rem' }}>
        <span style={{ color: star <= value ? '#fbbf24' : '#2a2a3a' }}>★</span>
      </button>
    ))}
  </div>
);

const ScoreBar = ({ value }) => {
  const getColor = (val) => {
    if (val >= 65) return '#10b981';
    if (val >= 40) return '#eab308';
    return '#ef4444';
  };
  return (
    <div style={{ marginTop: '0.25rem' }}>
      <div style={{ backgroundColor: '#2a2a3a', borderRadius: '0.25rem', height: '4px', overflow: 'hidden' }}>
        <div style={{ width: `${value}%`, backgroundColor: getColor(value), height: '100%' }} />
      </div>
    </div>
  );
};

const SliderInput = ({ label, value, onChange }) => {
  const getColor = (val) => {
    if (val >= 65) return '#10b981';
    if (val >= 40) return '#eab308';
    return '#ef4444';
  };
  const getLabel = (val) => {
    if (val >= 65) return 'Stärke';
    if (val >= 40) return 'Durchschnitt';
    return 'Schwäche';
  };
  return (
    <div style={{ marginBottom: '0.75rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
        <span style={{ color: '#b8baff', fontSize: '0.875rem' }}>{label}</span>
        <span style={{ color: getColor(value), fontWeight: 'bold' }}>{getLabel(value)} ({value}%)</span>
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <span style={{ fontSize: '0.7rem', color: '#ef4444' }}>0%</span>
        <input
          type="range"
          min={0}
          max={100}
          value={value}
          onChange={(e) => onChange(parseInt(e.target.value))}
          style={{ flex: 1, accentColor: getColor(value) }}
        />
        <span style={{ fontSize: '0.7rem', color: '#10b981' }}>100%</span>
      </div>
    </div>
  );
};

const ScoutingList = () => {
  const [scoutingList, setScoutingList] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedPlayer, setSelectedPlayer] = useState(null);
  const [deleteTargetId, setDeleteTargetId] = useState(null);
  const [deleteTargetName, setDeleteTargetName] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [rating, setRating] = useState(3);
  const [note, setNote] = useState('');
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  
  const [strengthTalent, setStrengthTalent] = useState(70);
  const [strengthSpeed, setStrengthSpeed] = useState(70);
  const [strengthTactics, setStrengthTactics] = useState(70);
  const [strengthPassing, setStrengthPassing] = useState(70);
  const [strengthTechnique, setStrengthTechnique] = useState(70);
  const [strengthFitness, setStrengthFitness] = useState(70);
  const [weaknessTackling, setWeaknessTackling] = useState(30);

  const cleanName = (name) => name?.replace(/ \(\d+\)/, '') || '?';

  const getPositionGroup = (position) => {
    if (!position) return 'Andere';
    const pos = position.toLowerCase();
    if (pos.includes('goalkeeper') || pos.includes('torwart')) return 'Torwart';
    if (pos.includes('defender') || pos.includes('abwehr') || pos.includes('back')) return 'Abwehr';
    if (pos.includes('midfield') || pos.includes('mittelfeld')) return 'Mittelfeld';
    if (pos.includes('attack') || pos.includes('sturm') || pos.includes('forward')) return 'Stürmer';
    return 'Andere';
  };

  const positionOrder = { 'Torwart': 1, 'Abwehr': 2, 'Mittelfeld': 3, 'Stürmer': 4, 'Andere': 5 };

  const getScoreColor = (value) => {
    if (value >= 65) return '#10b981';
    if (value >= 40) return '#eab308';
    return '#ef4444';
  };

  const getTotalScoreColor = (score) => {
    if (score >= 80) return '#10b981';
    if (score >= 60) return '#eab308';
    return '#ef4444';
  };

  const getTotalScoreLabel = (score) => {
    if (score >= 80) return 'Top-Talent';
    if (score >= 60) return 'Beobachten';
    return 'Entwicklung nötig';
  };

  const calculateTotalScore = (player) => {
    const strengths = [
      player.talent || 70,
      player.speed || 70,
      player.tactics || 70,
      player.passing || 70,
      player.technique || 70,
      player.fitness || 70
    ];
    const weakness = player.tackling || 30;
    const avgStrength = strengths.reduce((a, b) => a + b, 0) / strengths.length;
    const total = avgStrength * 0.7 + (100 - weakness) * 0.3;
    return Math.min(100, Math.max(0, Math.round(total)));
  };

  useEffect(() => {
    loadScoutingList();
  }, []);

  const loadScoutingList = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/scouting');
      const withPositions = await Promise.all(
        response.data.map(async (item) => {
          try {
            const playerRes = await axios.get(`http://localhost:8080/api/sofifa/player/${item.playerId}`);
            return { ...item, position: playerRes.data.position };
          } catch {
            return { ...item, position: null };
          }
        })
      );
      setScoutingList(withPositions);
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

  const resetForm = () => {
    setSelectedPlayer(null);
    setSearchQuery('');
    setSearchResults([]);
    setRating(3);
    setNote('');
    setStrengthTalent(70);
    setStrengthSpeed(70);
    setStrengthTactics(70);
    setStrengthPassing(70);
    setStrengthTechnique(70);
    setStrengthFitness(70);
    setWeaknessTackling(30);
    setEditingId(null);
  };

  const addScouting = async () => {
    if (!selectedPlayer) return;
    try {
      await axios.post(`http://localhost:8080/api/scouting/${selectedPlayer.id}`, null, {
        params: { 
          playerName: selectedPlayer.name, 
          rating, 
          note,
          talent: strengthTalent,
          speed: strengthSpeed,
          tactics: strengthTactics,
          passing: strengthPassing,
          technique: strengthTechnique,
          fitness: strengthFitness,
          tackling: weaknessTackling
        }
      });
      setShowModal(false);
      resetForm();
      loadScoutingList();
    } catch (error) {
      console.error('Fehler beim Hinzufügen:', error);
    }
  };

  const updateScouting = async () => {
    if (!editingId) return;
    try {
      await axios.put(`http://localhost:8080/api/scouting/${editingId}`, null, {
        params: { 
          rating, 
          note,
          talent: strengthTalent,
          speed: strengthSpeed,
          tactics: strengthTactics,
          passing: strengthPassing,
          technique: strengthTechnique,
          fitness: strengthFitness,
          tackling: weaknessTackling
        }
      });
      setEditingId(null);
      setShowModal(false);
      resetForm();
      loadScoutingList();
    } catch (error) {
      console.error('Fehler beim Aktualisieren:', error);
    }
  };

  const handleDeleteClick = (id, name) => {
    setDeleteTargetId(id);
    setDeleteTargetName(name);
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (!deleteTargetId) return;
    try {
      await axios.delete(`http://localhost:8080/api/scouting/${deleteTargetId}`);
      setShowDeleteModal(false);
      setDeleteTargetId(null);
      setDeleteTargetName('');
      loadScoutingList();
    } catch (error) {
      console.error('Fehler beim Löschen:', error);
    }
  };

  const openEditModal = (item) => {
    setEditingId(item.id);
    setSelectedPlayer({ id: item.playerId, name: item.playerName });
    setRating(item.rating);
    setNote(item.note || '');
    setStrengthTalent(item.talent || 70);
    setStrengthSpeed(item.speed || 70);
    setStrengthTactics(item.tactics || 70);
    setStrengthPassing(item.passing || 70);
    setStrengthTechnique(item.technique || 70);
    setStrengthFitness(item.fitness || 70);
    setWeaknessTackling(item.tackling || 30);
    setShowModal(true);
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '2rem', color: 'white' }}>Laden...</div>;

  const groupedPlayers = { Torwart: [], Abwehr: [], Mittelfeld: [], Stürmer: [], Andere: [] };
  scoutingList.forEach(player => {
    const group = getPositionGroup(player.position);
    groupedPlayers[group].push(player);
  });

  return (
    <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '1.875rem', fontWeight: 'bold', color: 'white' }}>📋 Meine Scout-Liste</h1>
        <button onClick={() => setShowModal(true)} style={{ padding: '0.5rem 1rem', backgroundColor: '#6666ff', color: 'white', border: 'none', borderRadius: '0.5rem', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Plus className="w-5 h-5" /> Spieler hinzufügen
        </button>
      </div>

      {scoutingList.length === 0 ? (
        <div style={{ backgroundColor: '#1a1a2a', border: '1px solid #2a2a3a', borderRadius: '0.75rem', padding: '3rem', textAlign: 'center' }}>
          <p style={{ color: '#b8baff' }}>Keine Spieler in der Scout-Liste</p>
        </div>
      ) : (
        Object.entries(positionOrder).map(([groupName, order]) => {
          const players = groupedPlayers[groupName];
          if (players.length === 0) return null;
          return (
            <div key={groupName} style={{ marginBottom: '2rem' }}>
              <h2 style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#6666ff', marginBottom: '1rem', borderBottom: '1px solid #2a2a3a', paddingBottom: '0.5rem' }}>
                {groupName} ({players.length})
              </h2>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {players.map((item) => {
                  const totalScore = calculateTotalScore(item);
                  return (
                    <div key={item.id} style={{ backgroundColor: '#1a1a2a', border: '1px solid #2a2a3a', borderRadius: '0.75rem', padding: '1rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flex: 1 }}>
                          <div style={{ width: '48px', height: '48px', borderRadius: '50%', background: 'linear-gradient(135deg, #6666ff, #b8baff)', display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden' }}>
                            <img 
                              src={`https://tmssl.akamaized.net/images/portrait/header/${item.playerId}.png`}
                              alt={item.playerName}
                              style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                              onError={(e) => { e.target.onerror = null; e.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(item.playerName)}&background=6666ff&color=fff&size=48`; }}
                            />
                          </div>
                          <div>
                            <div style={{ fontWeight: '600', color: 'white' }}>{cleanName(item.playerName)}</div>
                            <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', fontSize: '0.875rem', color: '#b8baff' }}>
                              <div style={{ display: 'flex', gap: '0.125rem' }}>
                                {[1, 2, 3, 4, 5].map((star) => (
                                  <span key={star} style={{ color: star <= item.rating ? '#fbbf24' : '#2a2a3a' }}>★</span>
                                ))}
                              </div>
                              {item.note && <span>• {item.note}</span>}
                              <span>• {item.position?.split(' - ')[0] || '?'}</span>
                            </div>
                          </div>
                        </div>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          <button onClick={() => openEditModal(item)} style={{ padding: '0.5rem', background: '#2a2a3a', border: 'none', borderRadius: '0.5rem', cursor: 'pointer' }}>✏️</button>
                          <button onClick={() => handleDeleteClick(item.id, item.playerName)} style={{ padding: '0.5rem', background: '#2a2a3a', border: 'none', borderRadius: '0.5rem', cursor: 'pointer', color: '#ef4444' }}>🗑️</button>
                        </div>
                      </div>
                      
                      <div style={{ marginTop: '0.75rem', paddingTop: '0.75rem', borderTop: '1px solid #2a2a3a' }}>
                        <div style={{ fontSize: '0.75rem', color: '#b8baff', marginBottom: '0.5rem' }}>📊 Scouting-Bewertung</div>
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '0.75rem' }}>
                          <div>
                            <div style={{ fontSize: '0.7rem', display: 'flex', justifyContent: 'space-between' }}>
                              <span>Talent</span>
                              <span style={{ color: getScoreColor(item.talent || 70) }}>{item.talent || 70}%</span>
                            </div>
                            <ScoreBar value={item.talent || 70} />
                          </div>
                          <div>
                            <div style={{ fontSize: '0.7rem', display: 'flex', justifyContent: 'space-between' }}>
                              <span>Speed</span>
                              <span style={{ color: getScoreColor(item.speed || 70) }}>{item.speed || 70}%</span>
                            </div>
                            <ScoreBar value={item.speed || 70} />
                          </div>
                          <div>
                            <div style={{ fontSize: '0.7rem', display: 'flex', justifyContent: 'space-between' }}>
                              <span>Taktik</span>
                              <span style={{ color: getScoreColor(item.tactics || 70) }}>{item.tactics || 70}%</span>
                            </div>
                            <ScoreBar value={item.tactics || 70} />
                          </div>
                          <div>
                            <div style={{ fontSize: '0.7rem', display: 'flex', justifyContent: 'space-between' }}>
                              <span>Passen</span>
                              <span style={{ color: getScoreColor(item.passing || 70) }}>{item.passing || 70}%</span>
                            </div>
                            <ScoreBar value={item.passing || 70} />
                          </div>
                          <div>
                            <div style={{ fontSize: '0.7rem', display: 'flex', justifyContent: 'space-between' }}>
                              <span>Technik</span>
                              <span style={{ color: getScoreColor(item.technique || 70) }}>{item.technique || 70}%</span>
                            </div>
                            <ScoreBar value={item.technique || 70} />
                          </div>
                          <div>
                            <div style={{ fontSize: '0.7rem', display: 'flex', justifyContent: 'space-between' }}>
                              <span>Fitness</span>
                              <span style={{ color: getScoreColor(item.fitness || 70) }}>{item.fitness || 70}%</span>
                            </div>
                            <ScoreBar value={item.fitness || 70} />
                          </div>
                          <div>
                            <div style={{ fontSize: '0.7rem', display: 'flex', justifyContent: 'space-between' }}>
                              <span>Zweikampf</span>
                              <span style={{ color: getScoreColor(100 - (item.tackling || 30)) }}>{100 - (item.tackling || 30)}%</span>
                            </div>
                            <ScoreBar value={100 - (item.tackling || 30)} />
                          </div>
                        </div>
                      </div>
                      
                      <div style={{ marginTop: '0.75rem', paddingTop: '0.5rem', borderTop: '1px solid #2a2a3a', display: 'flex', justifyContent: 'flex-end' }}>
                        <div style={{ textAlign: 'right' }}>
                          <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: getTotalScoreColor(totalScore) }}>{totalScore}%</div>
                          <div style={{ fontSize: '0.7rem', color: '#b8baff' }}>{getTotalScoreLabel(totalScore)}</div>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          );
        })
      )}

      {/* Delete Modal */}
      {showDeleteModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.8)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1001,
        }}>
          <div style={{
            backgroundColor: '#1a1a2a',
            border: '1px solid #2a2a3a',
            borderRadius: '0.75rem',
            padding: '1.5rem',
            maxWidth: '400px',
            width: '90%',
            textAlign: 'center',
          }}>
            <div style={{ fontSize: '3rem', marginBottom: '0.5rem' }}>🗑️</div>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'white', marginBottom: '0.5rem' }}>Spieler entfernen</h3>
            <p style={{ color: '#b8baff', marginBottom: '1.5rem' }}>
              Möchtest du <strong style={{ color: '#6666ff' }}>{cleanName(deleteTargetName)}</strong> wirklich aus deiner Scout-Liste entfernen?
            </p>
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
              <button
                onClick={() => setShowDeleteModal(false)}
                style={{
                  padding: '0.5rem 1.5rem',
                  backgroundColor: '#2a2a3a',
                  color: '#b8baff',
                  border: 'none',
                  borderRadius: '0.5rem',
                  cursor: 'pointer',
                }}
              >
                Abbrechen
              </button>
              <button
                onClick={confirmDelete}
                style={{
                  padding: '0.5rem 1.5rem',
                  backgroundColor: '#ef4444',
                  color: 'white',
                  border: 'none',
                  borderRadius: '0.5rem',
                  cursor: 'pointer',
                }}
              >
                Löschen
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Add/Edit Modal */}
      {showModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.8)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000,
        }}>
          <div style={{
            backgroundColor: '#1a1a2a',
            border: '1px solid #2a2a3a',
            borderRadius: '0.75rem',
            padding: '1.5rem',
            maxWidth: '500px',
            width: '90%',
            maxHeight: '90vh',
            overflow: 'auto',
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
              <h2 style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'white' }}>{editingId ? 'Bewertung bearbeiten' : 'Spieler hinzufügen'}</h2>
              <button onClick={() => { setShowModal(false); resetForm(); }} style={{ background: 'none', border: 'none', color: '#b8baff', cursor: 'pointer' }}><X className="w-6 h-6" /></button>
            </div>

            {!selectedPlayer && !editingId && (
              <div style={{ marginBottom: '1rem' }}>
                <input
                  type="text"
                  placeholder="Spieler suchen..."
                  value={searchQuery}
                  onChange={(e) => { setSearchQuery(e.target.value); searchPlayers(e.target.value); }}
                  style={{ width: '100%', padding: '0.5rem 1rem', backgroundColor: '#0c0c16', border: '1px solid #2a2a3a', borderRadius: '0.5rem', color: 'white' }}
                />
                {searchResults.map(p => (
                  <div
                    key={p.id}
                    onClick={() => setSelectedPlayer(p)}
                    style={{ padding: '0.75rem', cursor: 'pointer', borderBottom: '1px solid #2a2a3a' }}
                  >
                    <div style={{ fontWeight: '500', color: 'white' }}>{cleanName(p.name)}</div>
                    <div style={{ fontSize: '0.875rem', color: '#b8baff' }}>{p.club}</div>
                  </div>
                ))}
              </div>
            )}

            {selectedPlayer && (
              <div style={{ marginBottom: '1rem', padding: '0.75rem', backgroundColor: '#0c0c16', borderRadius: '0.5rem' }}>
                <div style={{ fontWeight: '500', color: 'white' }}>{cleanName(selectedPlayer.name)}</div>
              </div>
            )}

            <StarRating value={rating} onChange={setRating} />
            <textarea
              value={note}
              onChange={(e) => setNote(e.target.value)}
              rows="2"
              placeholder="Notiz..."
              style={{ width: '100%', padding: '0.5rem 1rem', backgroundColor: '#0c0c16', border: '1px solid #2a2a3a', borderRadius: '0.5rem', color: 'white', marginBottom: '1rem' }}
            />

            <h3 style={{ color: 'white', fontSize: '0.875rem', marginBottom: '0.5rem' }}>💪 Stärken & ⚠️ Schwächen</h3>
            <SliderInput label="Talent" value={strengthTalent} onChange={setStrengthTalent} />
            <SliderInput label="Speed" value={strengthSpeed} onChange={setStrengthSpeed} />
            <SliderInput label="Taktik" value={strengthTactics} onChange={setStrengthTactics} />
            <SliderInput label="Passen" value={strengthPassing} onChange={setStrengthPassing} />
            <SliderInput label="Technik" value={strengthTechnique} onChange={setStrengthTechnique} />
            <SliderInput label="Fitness" value={strengthFitness} onChange={setStrengthFitness} />
            <SliderInput label="Zweikampf" value={weaknessTackling} onChange={setWeaknessTackling} />

            <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1rem' }}>
              <button onClick={() => { setShowModal(false); resetForm(); }} style={{ flex: 1, padding: '0.5rem', backgroundColor: '#2a2a3a', color: 'white', border: 'none', borderRadius: '0.5rem', cursor: 'pointer' }}>Abbrechen</button>
              <button onClick={editingId ? updateScouting : addScouting} style={{ flex: 1, padding: '0.5rem', backgroundColor: '#6666ff', color: 'white', border: 'none', borderRadius: '0.5rem', cursor: 'pointer' }}>{editingId ? 'Speichern' : 'Hinzufügen'}</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ScoutingList;