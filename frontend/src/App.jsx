import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ViewBacaan from './pages/ViewBacaan';
import CreateBacaan from './pages/CreateBacaan';
import EditBacaan from "./pages/EditBacaan.jsx";
import DeleteConfirmBacaan from './pages/DeleteConfirmBacaan';
import './App.css';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<ViewBacaan />} />
                <Route path="/create" element={<CreateBacaan />} />
                <Route path="/edit/:id" element={<EditBacaan />} />
                <Route path="/delete/:id" element={<DeleteConfirmBacaan />} />
            </Routes>
        </Router>
    );
}

export default App;