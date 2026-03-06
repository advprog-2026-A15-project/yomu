import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

const API_BASE = 'http://localhost:8080';

export default function EditComment() {
    const { id, bacaanId } = useParams();
    const navigate = useNavigate();
    const [isiKomentar, setIsiKomentar] = useState('');
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        fetch(`${API_BASE}/api/comment/${id}`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error('Komentar tidak ditemukan');
                }
                return res.json();
            })
            .then((data) => setIsiKomentar(data.isiKomentar || ''))
            .catch((err) => setError(err.message || 'Gagal memuat komentar'));
    }, [id]);

    const handleSubmit = (e) => {
        e.preventDefault();
        setError('');
        setIsSubmitting(true);

        fetch(`${API_BASE}/api/comment/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                isiKomentar
            })
        })
            .then((res) => {
                if (!res.ok) {
                    throw new Error('Gagal memperbarui komentar');
                }
                navigate(`/bacaan/${bacaanId}`);
            })
            .catch((err) => setError(err.message || 'Terjadi kesalahan'))
            .finally(() => setIsSubmitting(false));
    };

    return (
        <div className="page-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
            <div className="form-card">
                <Link to={`/bacaan/${bacaanId}`} style={{ color: 'var(--blue)', textDecoration: 'none' }}>← Kembali ke detail</Link>
                <h2 style={{ color: 'var(--lavender)', margin: '20px 0' }}>Edit Komentar</h2>

                {error ? <p style={{ color: 'var(--red)' }}>{error}</p> : null}

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    <textarea
                        className="input-entry"
                        rows="6"
                        placeholder="Edit komentar kamu"
                        value={isiKomentar}
                        onChange={(e) => setIsiKomentar(e.target.value)}
                        required
                    />
                    <button
                        type="submit"
                        className="btn"
                        style={{ backgroundColor: 'var(--blue)', color: 'var(--base)' }}
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? 'Menyimpan...' : 'Simpan Perubahan'}
                    </button>
                </form>
            </div>
        </div>
    );
}

