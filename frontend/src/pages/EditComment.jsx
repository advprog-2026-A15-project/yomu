import {useEffect, useState} from 'react';
import {Link, useLocation, useNavigate, useParams} from 'react-router-dom';
import {getToken} from '../services/authService';

const API_BASE = 'http://localhost:8080';

async function readErrorMessage(res, fallbackMessage) {
    try {
        const contentType = res.headers.get('content-type') || '';
        if (contentType.includes('application/json')) {
            const data = await res.json();
            if (typeof data?.error === 'string' && data.error.trim()) {
                return data.error;
            }
            if (typeof data?.message === 'string' && data.message.trim()) {
                return data.message;
            }
        } else {
            const text = await res.text();
            if (text && text.trim()) {
                return text;
            }
        }
    } catch {
        // fallback message is returned below
    }

    return fallbackMessage;
}

export default function EditComment() {
    const {commentId, bacaanId} = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    const invalidCommentId = !commentId || commentId === 'undefined';
    const [isiKomentar, setIsiKomentar] = useState(location.state?.comment?.isiKomentar || '');
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        const token = getToken();

        if (!token) {
            navigate('/login');
            return;
        }

        const routedComment = location.state?.comment;
        if (routedComment?.id === commentId && routedComment?.isiKomentar != null) {
            return;
        }

        if (invalidCommentId) {
            return;
        }

        let isMounted = true;

        fetch(`${API_BASE}/api/comment/${commentId}`, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`
            }
        })
            .then(async (res) => {
                if (res.status === 401) {
                    navigate('/login');
                    return null;
                }

                if (res.status === 403) {
                    throw new Error(await readErrorMessage(res, 'Anda tidak memiliki izin untuk mengedit komentar ini'));
                }

                if (!res.ok) {
                    throw new Error(await readErrorMessage(res, 'Komentar tidak ditemukan'));
                }

                return res.json();
            })
            .then((data) => {
                if (isMounted && data) {
                    setIsiKomentar(data.isiKomentar || '');
                }
            })
            .catch((err) => {
                if (isMounted) {
                    setError(err.message || 'Gagal memuat komentar');
                }
            });

        return () => {
            isMounted = false;
        };
    }, [commentId, invalidCommentId, location.state, navigate]);

    const handleSubmit = (e) => {
        e.preventDefault();
        setError('');

        const token = getToken();
        if (!token) {
            navigate('/login');
            return;
        }

        if (invalidCommentId) {
            setError('Komentar tidak ditemukan');
            return;
        }

        setIsSubmitting(true);

        fetch(`${API_BASE}/api/comment/${commentId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`
            },
            body: JSON.stringify({
                isiKomentar
            })
        })
            .then(async (res) => {
                if (res.status === 401) {
                    navigate('/login');
                    return;
                }

                if (res.status === 403) {
                    throw new Error(await readErrorMessage(res, 'Anda tidak memiliki izin untuk mengedit komentar ini'));
                }

                if (res.status === 404) {
                    throw new Error(await readErrorMessage(res, 'Komentar tidak ditemukan'));
                }

                if (!res.ok) {
                    throw new Error(await readErrorMessage(res, 'Gagal memperbarui komentar'));
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
                <h2 style={{color: 'var(--lavender)', margin: '20px 0'}}>Edit Komentar</h2>

                {error || invalidCommentId ? <p style={{color: 'var(--red)'}}>{error || 'Komentar tidak ditemukan'}</p> : null}

                <form onSubmit={handleSubmit} style={{display: 'flex', flexDirection: 'column', gap: '20px'}}>
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
                        style={{backgroundColor: 'var(--blue)', color: 'var(--base)'}}
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? 'Menyimpan...' : 'Simpan Perubahan'}
                    </button>
                </form>
            </div>
        </div>
    );
}
