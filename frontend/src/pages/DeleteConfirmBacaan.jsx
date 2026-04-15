import {useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {getToken} from '../services/authService';

export default function DeleteConfirmBacaan() {
    const {id} = useParams();
    const [judul, setJudul] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [deleting, setDeleting] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const controller = new AbortController();
        const token = getToken();

        if (!token) {
            navigate('/login');
            return;
        }

        const loadBacaan = async () => {
            setLoading(true);
            setError('');
            try {
                const res = await fetch(`http://localhost:8080/api/bacaan/${id}`, {
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${token}`,
                    },
                    signal: controller.signal,
                });

                if (res.status === 401 || res.status === 403) {
                    navigate('/login');
                    return;
                }

                if (res.status === 404) {
                    setError('Bacaan tidak ditemukan.');
                    return;
                }

                if (!res.ok) {
                    const text = await res.text();
                    setError(text || 'Gagal mengambil data bacaan');
                    return;
                }

                const data = await res.json();
                setJudul(data?.judul || '(tanpa judul)');
            } catch (err) {
                if (err.name !== 'AbortError') {
                    setError(err.message || 'Terjadi kesalahan saat memuat data');
                }
            } finally {
                setLoading(false);
            }
        };

        loadBacaan();
        return () => controller.abort();
    }, [id, navigate]);

    const handleDelete = async () => {
        const token = getToken();
        if (!token) {
            navigate('/login');
            return;
        }

        setDeleting(true);
        setError('');
        try {
            const res = await fetch(`http://localhost:8080/api/bacaan/${id}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (res.status === 401 || res.status === 403) {
                navigate('/login');
                return;
            }

            if (res.status === 404) {
                setError('Bacaan tidak ditemukan atau sudah dihapus.');
                return;
            }

            if (!res.ok) {
                let text = await res.text();
                try {
                    const jsonError = JSON.parse(text);
                    if (jsonError.error && jsonError.error.includes('Referential integrity constraint')) {
                        setError('Bacaan tidak dapat dihapus karena masih ada komentar yang terkait. Silakan hapus semua komentar terlebih dahulu.');
                    } else {
                        setError(jsonError.error || 'Gagal menghapus bacaan');
                    }
                } catch {
                    setError(text || 'Gagal menghapus bacaan');
                }
                return;
            }

            navigate('/');
        } catch (err) {
            setError(err.message || 'Terjadi kesalahan saat menghapus bacaan');
        } finally {
            setDeleting(false);
        }
    };

    return (
        <div className="page-container" style={{alignItems: 'center', justifyContent: 'center'}}>
            <div className="form-card" style={{textAlign: 'center', borderColor: 'var(--red)'}}>
                <h2 style={{color: 'var(--red)'}}>Konfirmasi Hapus</h2>

                {loading && <div className="status-note">Memuat data bacaan...</div>}
                {!loading && error && <div className="status-error">{error}</div>}

                {!loading && !error && (
                    <>
                        <p>Apakah Anda yakin ingin menghapus bacaan:</p>
                        <p><strong style={{color: 'var(--lavender)', fontSize: '20px'}}>&quot;{judul}&quot;</strong></p>
                        <p style={{fontSize: '12px', color: 'var(--subtext0)'}}>ID: {id}</p>
                    </>
                )}

                <div style={{marginTop: '30px', display: 'flex', gap: '15px', justifyContent: 'center'}}>
                    <button className="btn btn-delete" onClick={handleDelete} disabled={loading || !!error || deleting}
                            type="button">
                        {deleting ? 'Menghapus...' : 'Ya, Hapus'}
                    </button>
                    <Link to="/">
                        <button className="btn btn-ghost" type="button">Batal</button>
                    </Link>
                </div>
            </div>
        </div>
    );
}