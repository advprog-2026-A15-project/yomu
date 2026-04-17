import {Link, useNavigate, useParams} from 'react-router-dom';
import {useState} from 'react';
import {getToken} from '../services/authService';

const API_BASE = '';

export default function DeleteConfirmComment() {
    const {commentId, bacaanId} = useParams();
    const navigate = useNavigate();
    const [isDeleting, setIsDeleting] = useState(false);
    const [error, setError] = useState('');

    const handleDelete = () => {
        const token = getToken();

        if (!token) {
            navigate('/login');
            return;
        }

        setIsDeleting(true);
        setError('');

        fetch(`${API_BASE}/api/comment/${commentId}`, {
            method: 'DELETE',
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then((res) => {
                if (res.status === 401) {
                    navigate('/login');
                    return;
                }

                if (res.status === 403) {
                    throw new Error('Anda tidak memiliki izin untuk menghapus komentar ini');
                }

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
        <div className="page-container" style={{alignItems: 'center', justifyContent: 'center'}}>
            <div className="form-card" style={{textAlign: 'center', borderColor: 'var(--red)'}}>
                <h2 style={{color: 'var(--red)'}}>⚠ Konfirmasi Hapus Komentar</h2>
                <p>Apakah Anda yakin ingin menghapus komentar ini?</p>
                {error ? <p style={{color: 'var(--red)'}}>{error}</p> : null}

                <div style={{marginTop: '30px', display: 'flex', gap: '15px', justifyContent: 'center'}}>
                    <button
                        className="btn"
                        onClick={handleDelete}
                        style={{backgroundColor: 'var(--red)', color: 'var(--base)'}}
                        disabled={isDeleting}
                    >
                        {isDeleting ? 'Menghapus...' : 'Ya, Hapus'}
                    </button>
                    <Link to={`/bacaan/${bacaanId}`}>
                        <button className="btn" style={{backgroundColor: 'var(--surface1)', color: 'var(--text)'}}>
                            Batal
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    );
}
