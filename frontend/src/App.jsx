import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { getToken } from './services/authService';

// Halaman autentikasi
import Login from './pages/Login';
import Register from './pages/Register';

// Halaman bacaan (asumsi sudah ada)
import ViewBacaan from './pages/ViewBacaan';
import CreateBacaan from './pages/CreateBacaan';
import EditBacaan from './pages/EditBacaan';
import DeleteConfirmBacaan from './pages/DeleteConfirmBacaan';
import DetailBacaan from './pages/DetailBacaan';
import CreateComment from './pages/CreateComment';
import EditComment from './pages/EditComment';
import DeleteConfirmComment from './pages/DeleteConfirmComment';

import './App.css';

// Komponen untuk melindungi route yang memerlukan autentikasi
const ProtectedRoute = ({ children }) => {
  const token = getToken();
  const location = useLocation();

  if (!token) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  return children;
};

// Komponen untuk route autentikasi (login/register) agar tidak bisa diakses jika sudah login
const AuthRoute = ({ children }) => {
  const token = getToken();
  if (token) {
    return <Navigate to="/" replace />;
  }
  return children;
};

function App() {
  return (
    <Router>
      <Routes>
        {/* Route autentikasi */}
        <Route path="/login" element={<AuthRoute><Login /></AuthRoute>} />
        <Route path="/register" element={<AuthRoute><Register /></AuthRoute>} />

        {/* Route bacaan yang dilindungi */}
        <Route path="/" element={<ProtectedRoute><ViewBacaan /></ProtectedRoute>} />
        <Route path="/create" element={<ProtectedRoute><CreateBacaan /></ProtectedRoute>} />
        <Route path="/edit/:id" element={<ProtectedRoute><EditBacaan /></ProtectedRoute>} />
        <Route path="/delete/:id" element={<ProtectedRoute><DeleteConfirmBacaan /></ProtectedRoute>} />
        <Route path="/bacaan/:id" element={<ProtectedRoute><DetailBacaan /></ProtectedRoute>} />
        <Route path="/bacaan/:id/comment/new" element={<ProtectedRoute><CreateComment /></ProtectedRoute>} />
        <Route path="/bacaan/:bacaanId/comment/:id/edit" element={<ProtectedRoute><EditComment /></ProtectedRoute>} />
        <Route path="/bacaan/:bacaanId/comment/:id/delete" element={<ProtectedRoute><DeleteConfirmComment /></ProtectedRoute>} />


        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;