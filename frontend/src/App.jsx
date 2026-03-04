import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ViewBacaan from './pages/ViewBacaan';
import CreateBacaan from './pages/CreateBacaan';
import EditBacaan from "./pages/EditBacaan.jsx";
import DeleteConfirmBacaan from './pages/DeleteConfirmBacaan';
import DetailBacaan from './pages/DetailBacaan';
import CreateComment from './pages/CreateComment';
import EditComment from './pages/EditComment';
import DeleteConfirmComment from './pages/DeleteConfirmComment';
import './App.css';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<ViewBacaan />} />
                <Route path="/create" element={<CreateBacaan />} />
                <Route path="/edit/:id" element={<EditBacaan />} />
                <Route path="/delete/:id" element={<DeleteConfirmBacaan />} />
                <Route path="/bacaan/:id" element={<DetailBacaan />} />
                <Route path="/bacaan/:id/comment/new" element={<CreateComment />} />
                <Route path="/bacaan/:bacaanId/comment/:id/edit" element={<EditComment />} />
                <Route path="/bacaan/:bacaanId/comment/:id/delete" element={<DeleteConfirmComment />} />
            </Routes>
        </Router>
    );
}

export default App;