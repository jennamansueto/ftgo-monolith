import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { RestaurantDashboard, OrderDetailsPage } from './features/restaurant/pages';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<RestaurantDashboard />} />
        <Route path="/orders/:orderId" element={<OrderDetailsPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
