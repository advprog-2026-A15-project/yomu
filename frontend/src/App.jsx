import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import ViewBacaan from './pages/ViewBacaan';
import CreateBacaan from './pages/CreateBacaan';
import EditBacaan from "./pages/EditBacaan";
import DeleteConfirmBacaan from './pages/DeleteConfirmBacaan';
import Login from './pages/Login';
import Register from './pages/Register';
import './App.css'; // jika ada CSS tambahan, pastikan import

const ProtectedRoute = ({ children }) => {
    const user = localStorage.getItem('user');
    if (!user) {
        return <Navigate to="/login" replace />;
    }
    return children;
};

const AuthRoute = ({ children }) => {
    const user = localStorage.getItem('user');
    if (user) {
        return <Navigate to="/" replace />;
    }
    return children;
};

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<AuthRoute><Login /></AuthRoute>} />
                <Route path="/register" element={<AuthRoute><Register /></AuthRoute>} />
                <Route path="/" element={<ProtectedRoute><ViewBacaan /></ProtectedRoute>} />
                <Route path="/create" element={<ProtectedRoute><CreateBacaan /></ProtectedRoute>} />
                <Route path="/edit/:id" element={<ProtectedRoute><EditBacaan /></ProtectedRoute>} />
                <Route path="/delete/:id" element={<ProtectedRoute><DeleteConfirmBacaan /></ProtectedRoute>} />
            </Routes>
        </Router>
    );
}

export default App;