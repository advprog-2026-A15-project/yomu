import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getToken } from '../services/authService';

const API_BASE = 'http://localhost:8080';

export default function DetailBacaan() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [bacaan, setBacaan] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const controller = new AbortController();
    const token = getToken();

    if (!token) {
      navigate('/login');
      return;
    }

    const load = async () => {
      setLoading(true);
      setError('');
      try {
        const headers = {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        };

        const [bacaanRes, commentRes] = await Promise.all([
          fetch(`${API_BASE}/api/bacaan/${id}`, { headers, signal: controller.signal }),
          fetch(`${API_BASE}/api/comment`, { headers, signal: controller.signal }),
        ]);

        if (
          bacaanRes.status === 401 ||
          bacaanRes.status === 403 ||
          commentRes.status === 401 ||
          commentRes.status === 403
        ) {
          navigate('/login');
          return;
        }

        if (bacaanRes.status === 404) {
          setError('Bacaan tidak ditemukan.');
          return;
        }

        if (!bacaanRes.ok) {
          const text = await bacaanRes.text();
          throw new Error(text || 'Gagal mengambil detail bacaan');
        }

        if (!commentRes.ok) {
          const text = await commentRes.text();
          throw new Error(text || 'Gagal mengambil daftar komentar');
        }

        const bacaanData = await bacaanRes.json();
        const commentData = await commentRes.json();

        setBacaan(bacaanData);
        setComments(Array.isArray(commentData) ? commentData : []);
      } catch (err) {
        if (err.name !== 'AbortError') {
          setError(err.message || 'Terjadi kesalahan saat memuat data.');
        }
      } finally {
        setLoading(false);
      }
    };

    load();
    return () => controller.abort();
  }, [id, navigate]);

  const relatedComments = useMemo(
    () => comments.filter((comment) => comment?.bacaan?.id === id || comment?.bacaanId === id),
    [comments, id]
  );

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
          <button className="btn btn-add" type="button">+ Tambah Komentar</button>
        </Link>
      </div>

      <section className="form-card" style={{ maxWidth: 'none' }}>
        <h2 style={{ color: 'var(--lavender)', marginTop: 0 }}>{bacaan.judul}</h2>
        <p style={{ color: 'var(--subtext0)', marginBottom: '16px', wordBreak: 'break-all' }}>ID: {bacaan.id}</p>
        <p style={{ whiteSpace: 'pre-wrap', lineHeight: 1.6 }}>{bacaan.isiTeks}</p>
      </section>

      <section className="form-card" style={{ maxWidth: 'none' }}>
        <h3 style={{ color: 'var(--lavender)', marginTop: 0 }}>Komentar ({relatedComments.length})</h3>
        {relatedComments.length === 0 ? (
          <p style={{ color: 'var(--subtext0)' }}>Belum ada komentar untuk bacaan ini.</p>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {relatedComments.map((comment) => (
              <article
                key={comment.id}
                style={{
                  backgroundColor: 'var(--base)',
                  border: '1px solid var(--surface1)',
                  borderRadius: '10px',
                  padding: '12px',
                }}
              >
                <p style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{comment.isiKomentar}</p>
                <small style={{ color: 'var(--subtext0)' }}>
                  {comment.createdAt ? new Date(comment.createdAt).toLocaleString() : ''}
                </small>
                <div style={{ display: 'flex', gap: '8px', marginTop: '8px', justifyContent: 'flex-end' }}>
                  <Link to={`/bacaan/${id}/comment/${comment.id}/edit`}>
                    <button className="btn btn-edit" style={{ padding: '4px 12px', fontSize: '12px' }} type="button">Edit</button>
                  </Link>
                  <Link to={`/bacaan/${id}/comment/${comment.id}/delete`}>
                    <button className="btn btn-delete" style={{ padding: '4px 12px', fontSize: '12px' }} type="button">Hapus</button>
                  </Link>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
