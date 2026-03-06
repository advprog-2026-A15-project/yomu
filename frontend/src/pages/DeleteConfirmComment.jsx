import { useNavigate, useParams, Link } from 'react-router-dom';
import { useState } from 'react';

const API_BASE = 'http://localhost:8080';

export default function DeleteConfirmComment() {
    const { id, bacaanId } = useParams();
    const navigate = useNavigate();
    const [isDeleting, setIsDeleting] = useState(false);
    const [error, setError] = useState('');

    const handleDelete = () => {
        setIsDeleting(true);
        setError('');

        fetch(`${API_BASE}/api/comment/${id}`, { method: 'DELETE' })
            .then((res) => {
                if (!res.ok) {
                    throw new Error('Gagal menghapus komentar');
                }
                navigate(`/bacaan/${bacaanId}`);
            })
            .catch((err) => {
                setError(err.message || 'Terjadi kesalahan');
                setIsDeleting(false);
            });
    };

    return (
        <div className="page-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
            <div className="form-card" style={{ textAlign: 'center', borderColor: 'var(--red)' }}>
                <h2 style={{ color: 'var(--red)' }}>⚠ Konfirmasi Hapus Komentar</h2>
                <p>Apakah Anda yakin ingin menghapus komentar ini?</p>
                {error ? <p style={{ color: 'var(--red)' }}>{error}</p> : null}

                <div style={{ marginTop: '30px', display: 'flex', gap: '15px', justifyContent: 'center' }}>
                    <button
                        className="btn"
                        onClick={handleDelete}
                        style={{ backgroundColor: 'var(--red)', color: 'var(--base)' }}
                        disabled={isDeleting}
                    >
                        {isDeleting ? 'Menghapus...' : 'Ya, Hapus'}
                    </button>
                    <Link to={`/bacaan/${bacaanId}`}>
                        <button className="btn" style={{ backgroundColor: 'var(--surface1)', color: 'var(--text)' }}>
                            Batal
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    );
}

