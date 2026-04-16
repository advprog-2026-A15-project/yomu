import {useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {getToken} from '../services/authService';

const API_BASE = 'http://localhost:8080';

export default function CreateComment() {
    const {bacaanId} = useParams();
    const navigate = useNavigate();
    const [judul, setJudul] = useState('');
    const [isiKomentar, setIsiKomentar] = useState('');
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        const token = getToken();

        if (!token) {
            navigate('/login');
            return;
        }

        fetch(`${API_BASE}/api/bacaan/${bacaanId}`, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`
            }
        })
            .then((res) => {
                if (res.status === 401 || res.status === 403) {
                    navigate('/login');
                    return null;
                }

                if (!res.ok) {
                    throw new Error('Bacaan tidak ditemukan');
                }
                return res.json();
            })
            .then((data) => {
                if (data) {
                    setJudul(data.judul || '');
                }
            })
            .catch((err) => setError(err.message || 'Gagal memuat bacaan'));
    }, [bacaanId, navigate]);

    const handleSubmit = (e) => {
        e.preventDefault();
        setError('');

        const token = getToken();
        if (!token) {
            navigate('/login');
            return;
        }

        setIsSubmitting(true);

        fetch(`${API_BASE}/api/comment`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`
            },
            body: JSON.stringify({
                isiKomentar,
                bacaanId
            })
        })
            .then((res) => {
                if (res.status === 401 || res.status === 403) {
                    navigate('/login');
                    return;
                }

                if (!res.ok) {
                    throw new Error('Gagal menambahkan komentar');
                }
                navigate(`/bacaan/${bacaanId}`);
            })
            .catch((err) => setError(err.message || 'Terjadi kesalahan'))
            .finally(() => setIsSubmitting(false));
    };

    return (
        <div className="page-container" style={{alignItems: 'center', justifyContent: 'center'}}>
            <div className="form-card">
                <Link to={`/bacaan/${bacaanId}`} style={{color: 'var(--blue)', textDecoration: 'none'}}>← Kembali ke
                    detail</Link>
                <h2 style={{color: 'var(--lavender)', margin: '20px 0 8px 0'}}>Tambah Komentar</h2>
                <p style={{color: 'var(--subtext)', marginTop: 0}}>
                    Untuk bacaan: <strong>{judul || bacaanId}</strong>
                </p>

                {error ? <p style={{color: 'var(--red)'}}>{error}</p> : null}

                <form onSubmit={handleSubmit} style={{display: 'flex', flexDirection: 'column', gap: '20px'}}>
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
                        style={{backgroundColor: 'var(--green)', color: 'var(--base)'}}
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? 'Menyimpan...' : 'Simpan Komentar'}
                    </button>
                </form>
            </div>
        </div>
    );
}
