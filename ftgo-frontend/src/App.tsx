import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { RestaurantDashboard, MenuManagement } from './features/restaurant/pages';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<RestaurantDashboard />} />
        <Route path="/menu" element={<MenuManagement />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
