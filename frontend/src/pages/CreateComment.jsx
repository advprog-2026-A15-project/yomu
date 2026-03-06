import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

const API_BASE = 'http://localhost:8080';

export default function CreateComment() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [judul, setJudul] = useState('');
    const [isiKomentar, setIsiKomentar] = useState('');
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        fetch(`${API_BASE}/api/bacaan/${id}`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error('Bacaan tidak ditemukan');
                }
                return res.json();
            })
            .then((data) => setJudul(data.judul || ''))
            .catch((err) => setError(err.message || 'Gagal memuat bacaan'));
    }, [id]);

    const handleSubmit = (e) => {
        e.preventDefault();
        setError('');
        setIsSubmitting(true);

        fetch(`${API_BASE}/api/comment`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                isiKomentar,
                bacaanId: id
            })
        })
            .then((res) => {
                if (!res.ok) {
                    throw new Error('Gagal menambahkan komentar');
                }
                navigate(`/bacaan/${id}`);
            })
            .catch((err) => setError(err.message || 'Terjadi kesalahan'))
            .finally(() => setIsSubmitting(false));
    };

    return (
        <div className="page-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
            <div className="form-card">
                <Link to={`/bacaan/${id}`} style={{ color: 'var(--blue)', textDecoration: 'none' }}>← Kembali ke detail</Link>
                <h2 style={{ color: 'var(--lavender)', margin: '20px 0 8px 0' }}>Tambah Komentar</h2>
                <p style={{ color: 'var(--subtext)', marginTop: 0 }}>
                    Untuk bacaan: <strong>{judul || id}</strong>
                </p>

                {error ? <p style={{ color: 'var(--red)' }}>{error}</p> : null}

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    <textarea
                        className="input-entry"
                        rows="6"
                        placeholder="Tulis komentar kamu"
                        value={isiKomentar}
                        onChange={(e) => setIsiKomentar(e.target.value)}
                        required
                    />
                    <button
                        type="submit"
                        className="btn"
                        style={{ backgroundColor: 'var(--green)', color: 'var(--base)' }}
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? 'Menyimpan...' : 'Simpan Komentar'}
                    </button>
                </form>
            </div>
        </div>
    );
}

