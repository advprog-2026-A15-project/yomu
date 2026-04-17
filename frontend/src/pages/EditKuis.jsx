import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getToken } from '../services/authService';

const API_BASE = 'http://localhost:8080';

export default function EditKuis() {
    const { bacaanId, kuisId } = useParams();
    const navigate = useNavigate();
    const [form, setForm] = useState({ pertanyaan: '', jawabanBenar: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = getToken();
        // Backend harus punya endpoint GET /api/kuis/{kuisId}
        fetch(`${API_BASE}/api/kuis/${kuisId}`, {
            headers: { Authorization: `Bearer ${token}` }
        })
        .then(res => res.json())
        .then(data => {
            setForm({ pertanyaan: data.pertanyaan, jawabanBenar: data.jawabanBenar });
            setLoading(false);
        })
        .catch(() => setError('Gagal memuat kuis'));
    }, [kuisId]);

    const handleUpdate = async (e) => {
        e.preventDefault();
        const token = getToken();
        try {
            const res = await fetch(`${API_BASE}/api/kuis/${kuisId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(form)
            });
            if (!res.ok) throw new Error('Gagal update kuis');
            navigate(`/bacaan/${bacaanId}`);
        } catch (err) {
            setError(err.message);
        }
    };

    if (loading) return <div className="status-note">Memuat...</div>;

    return (
        <div className="page-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
            <div className="form-card">
                <Link to={`/bacaan/${bacaanId}`} style={{ color: 'var(--blue)' }}>← Batal</Link>
                <h2 style={{ color: 'var(--lavender)', margin: '20px 0' }}>Edit Kuis</h2>
                <form onSubmit={handleUpdate} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    <textarea
                        className="input-entry"
                        value={form.pertanyaan}
                        onChange={(e) => setForm({ ...form, pertanyaan: e.target.value })}
                        required
                    />
                    <input
                        className="input-entry"
                        value={form.jawabanBenar}
                        onChange={(e) => setForm({ ...form, jawabanBenar: e.target.value })}
                        required
                    />
                    <button type="submit" className="btn btn-edit">Simpan Perubahan</button>
                </form>
            </div>
        </div>
    );
}