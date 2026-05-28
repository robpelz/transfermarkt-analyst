import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/layout/Layout';
import Dashboard from './components/dashboard/Dashboard';
import PlayerList from './components/players/PlayerList';
import PlayerDetail from './components/players/PlayerDetail';
import ClubList from './components/clubs/ClubList';
import PlayerCompare from './components/compare/PlayerCompare';
import ScoutingList from './components/scouting/ScoutingList';
import ClubDetail from './components/clubs/ClubDetail';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="players" element={<PlayerList />} />
          <Route path="players/:id" element={<PlayerDetail />} />
          <Route path="clubs" element={<ClubList />} />
          <Route path="analytics" element={<PlayerCompare />} />
          <Route path="scouting" element={<ScoutingList />} />
          <Route path="clubs/:id" element={<ClubDetail />} />

        </Route>
        
      </Routes>
    </BrowserRouter>

    
  );
}

export default App;