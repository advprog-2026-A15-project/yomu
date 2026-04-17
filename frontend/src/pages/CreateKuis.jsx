import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getToken } from '../services/authService';

const API_BASE = 'http://localhost:8080';

export default function CreateKuis() {
    const { bacaanId } = useParams();
    const navigate = useNavigate();
    const [judul, setJudul] = useState('');
    const [form, setForm] = useState({ pertanyaan: '', jawabanBenar: '' });
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        const token = getToken();
        if (!token) { navigate('/login'); return; }

        // Ambil info bacaan untuk judul saja
        fetch(`${API_BASE}/api/bacaan/${bacaanId}`, {
            headers: { Authorization: `Bearer ${token}` }
        })
        .then(res => res.json())
        .then(data => setJudul(data.judul))
        .catch(() => setError('Gagal memuat data bacaan'));
    }, [bacaanId, navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const token = getToken();
        setIsSubmitting(true);

        try {
            const res = await fetch(`${API_BASE}/api/kuis/${bacaanId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(form)
            });

            if (!res.ok) throw new Error('Gagal menambah kuis');
            navigate(`/bacaan/${bacaanId}`);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="page-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
            <div className="form-card">
                <Link to={`/bacaan/${bacaanId}`} style={{ color: 'var(--blue)', textDecoration: 'none' }}>← Kembali</Link>
                <h2 style={{ color: 'var(--lavender)', margin: '20px 0 8px 0' }}>Tambah Kuis Baru</h2>
                <p style={{ color: 'var(--subtext)', marginBottom: '20px' }}>Bacaan: <strong>{judul}</strong></p>

                {error && <p style={{ color: 'var(--red)' }}>{error}</p>}

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    <textarea
                        className="input-entry"
                        placeholder="Pertanyaan kuis..."
                        rows="4"
                        value={form.pertanyaan}
                        onChange={(e) => setForm({ ...form, pertanyaan: e.target.value })}
                        required
                    />
                    <input
                        className="input-entry"
                        placeholder="Jawaban Benar"
                        value={form.jawabanBenar}
                        onChange={(e) => setForm({ ...form, jawabanBenar: e.target.value })}
                        required
                    />
                    <button type="submit" className="btn btn-edit" disabled={isSubmitting}>
                        {isSubmitting ? 'Menyimpan...' : 'Simpan Kuis'}
                    </button>
                </form>
            </div>
        </div>
    );
}