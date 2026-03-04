import { useEffect, useMemo, useState } from 'react';
import { Link, useParams } from 'react-router-dom';

const API_BASE = 'http://localhost:8080';

export default function DetailBacaan() {
    const { id } = useParams();
    const [bacaan, setBacaan] = useState(null);
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        let isMounted = true;

        Promise.all([
            fetch(`${API_BASE}/api/bacaan/${id}`),
            fetch(`${API_BASE}/api/comment`)
        ])
            .then(async ([bacaanRes, commentRes]) => {
                if (!bacaanRes.ok) {
                    throw new Error('Gagal mengambil detail bacaan');
                }
                if (!commentRes.ok) {
                    throw new Error('Gagal mengambil daftar komentar');
                }

                const bacaanData = await bacaanRes.json();
                const commentData = await commentRes.json();

                if (!isMounted) {
                    return;
                }

                setBacaan(bacaanData);
                setComments(Array.isArray(commentData) ? commentData : []);
            })
            .catch((err) => {
                if (isMounted) {
                    setError(err.message || 'Terjadi kesalahan saat memuat data.');
                }
            })
            .finally(() => {
                if (isMounted) {
                    setLoading(false);
                }
            });

        return () => {
            isMounted = false;
        };
    }, [id]);

    const relatedComments = useMemo(() => {
        return comments.filter((comment) => {
            return comment?.bacaan?.id === id || comment?.bacaanId === id;
        });
    }, [comments, id]);

    if (loading) {
        return (
            <div className="page-container">
                <p>Memuat detail bacaan...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="page-container">
                <Link to="/" style={{ color: 'var(--blue)', textDecoration: 'none' }}>← Kembali</Link>
                <p style={{ color: 'var(--red)' }}>{error}</p>
            </div>
        );
    }

    if (!bacaan) {
        return (
            <div className="page-container">
                <Link to="/" style={{ color: 'var(--blue)', textDecoration: 'none' }}>← Kembali</Link>
                <p>Bacaan tidak ditemukan.</p>
            </div>
        );
    }

    return (
        <div className="page-container" style={{ gap: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Link to="/" style={{ color: 'var(--blue)', textDecoration: 'none' }}>← Kembali</Link>
                <Link to={`/bacaan/${id}/comment/new`}>
                    <button className="btn" style={{ backgroundColor: 'var(--green)', color: 'var(--base)' }}>
                        + Tambah Komentar
                    </button>
                </Link>
            </div>

            <section className="form-card" style={{ maxWidth: 'none' }}>
                <h2 style={{ color: 'var(--lavender)', marginTop: 0 }}>{bacaan.judul}</h2>
                <p style={{ color: 'var(--subtext)', marginBottom: '16px', wordBreak: 'break-all' }}>ID: {bacaan.id}</p>
                <p style={{ whiteSpace: 'pre-wrap', lineHeight: 1.6 }}>{bacaan.isiTeks}</p>
            </section>

            <section className="form-card" style={{ maxWidth: 'none' }}>
                <h3 style={{ color: 'var(--lavender)', marginTop: 0 }}>Komentar ({relatedComments.length})</h3>
                {relatedComments.length === 0 ? (
                    <p style={{ color: 'var(--subtext)' }}>Belum ada komentar untuk bacaan ini.</p>
                ) : (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                        {relatedComments.map((comment) => (
                            <article
                                key={comment.id}
                                style={{
                                    backgroundColor: 'var(--base)',
                                    border: '1px solid var(--surface1)',
                                    borderRadius: '10px',
                                    padding: '12px'
                                }}
                            >
                                <p style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{comment.isiKomentar}</p>
                                <small style={{ color: 'var(--subtext)' }}>
                                    {comment.createdAt ? new Date(comment.createdAt).toLocaleString() : ''}
                                </small>
                            </article>
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
}

