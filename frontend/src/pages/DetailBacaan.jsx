import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getToken } from '../services/authService';

const API_BASE = 'http://localhost:8080';

export default function DetailBacaan() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [bacaan, setBacaan] = useState(null);
  const [comments, setComments] = useState([]);
  const [myAchievements, setMyAchievements] = useState([]);
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

        try {
          const achievementRes = await fetch(`${API_BASE}/api/achievements/me`, { headers, signal: controller.signal });
          if (achievementRes.ok) {
            const achievementData = await achievementRes.json();
            setMyAchievements(Array.isArray(achievementData) ? achievementData : []);
          }
        } catch {
          // Achievement tidak kritikal
        }
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

  if (loading) return <div className="page-container"><p>Memuat detail bacaan...</p></div>;

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

      {/* --- KOTAK BACAAN --- */}
      <section className="form-card" style={{ maxWidth: 'none' }}>
        <h2 style={{ color: 'var(--lavender)', marginTop: 0 }}>{bacaan.judul}</h2>
        {bacaan.kategori && (
          <span style={{ backgroundColor: 'var(--blue)', color: 'var(--base)', padding: '4px 12px', borderRadius: '12px', fontSize: '14px', display: 'inline-block', marginBottom: '20px' }}>
            Kategori: {bacaan.kategori}
          </span>
        )}
        <p style={{ color: 'var(--subtext0)', marginBottom: '16px', wordBreak: 'break-all' }}>ID: {bacaan.id}</p>
        <p style={{ whiteSpace: 'pre-wrap', lineHeight: 1.6 }}>{bacaan.isiTeks}</p>
      </section>

      {/* --- KOTAK KUIS --- */}
      <section className="form-card" style={{ maxWidth: 'none' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h3 style={{ color: 'var(--lavender)', margin: 0 }}>Kuis Pemahaman</h3>

          <Link to={`/bacaan/${id}/kuis/create`}>
            <button className="btn btn-add" type="button" style={{ padding: '6px 12px', fontSize: '13px' }}>+ Tambah Soal</button>
          </Link>
        </div>

        {(!bacaan.quizzes || bacaan.quizzes.length === 0) ? (
          <p style={{ marginTop: 0 }}>Belum ada kuis untuk bacaan ini.</p>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', marginBottom: '20px' }}>

            {bacaan.quizzes.map((q, index) => (
              <div key={q.id} style={{ padding: '12px', backgroundColor: 'var(--base)', borderRadius: '8px', border: '1px solid var(--surface1)' }}>
                <p style={{ margin: '0 0 10px 0' }}><strong>Soal {index + 1}:</strong> {q.pertanyaan}</p>

                <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end' }}>
                  <Link to={`/bacaan/${id}/kuis/${q.id}/edit`}>
                    <button className="btn btn-edit" style={{ padding: '4px 10px', fontSize: '12px' }} type="button">Edit</button>
                  </Link>
                  <Link to={`/bacaan/${id}/kuis/${q.id}/delete`}>
                    <button className="btn btn-delete" style={{ padding: '4px 10px', fontSize: '12px' }} type="button">Hapus</button>
                  </Link>
                </div>
              </div>
            ))}

            {/* 👇 INI YANG BARU: TOMBOL PINDAH KE HALAMAN KUIS 👇 */}
            <div style={{ marginTop: '20px', textAlign: 'center', borderTop: '1px solid var(--surface1)', paddingTop: '20px' }}>
              <Link to={`/bacaan/${id}/mulai-kuis`}>
                <button className="btn" style={{ backgroundColor: 'var(--green)', color: 'var(--base)', padding: '12px 24px', fontSize: '16px', fontWeight: 'bold' }}>
                  📝 Mulai Kerjakan Kuis
                </button>
              </Link>
            </div>

          </div>
        )}

        <p style={{ marginTop: '16px', marginBottom: '6px', color: 'var(--subtext0)' }}>Achievement saya:</p>
        {myAchievements.length === 0 ? (
          <p style={{ margin: 0 }}>Belum ada achievement.</p>
        ) : (
          <ul style={{ margin: 0, paddingLeft: '18px' }}>
            {myAchievements.map((achievement) => (
              <li key={achievement.achievementId}>
                {achievement.name} - {achievement.description}
              </li>
            ))}
          </ul>
        )}
      </section>

      {/* --- KOTAK KOMENTAR --- */}
      <section className="form-card" style={{ maxWidth: 'none' }}>
        <h3 style={{ color: 'var(--lavender)', marginTop: 0 }}>Komentar ({relatedComments.length})</h3>
        {relatedComments.length === 0 ? (
          <p style={{ color: 'var(--subtext0)' }}>Belum ada komentar untuk bacaan ini.</p>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {relatedComments.map((comment) => (
              <article key={comment.id} style={{ backgroundColor: 'var(--base)', border: '1px solid var(--surface1)', borderRadius: '10px', padding: '12px' }}>
                <p style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{comment.isiKomentar}</p>
                <small style={{ color: 'var(--subtext0)', display: 'block' }}>
                  By: <strong>{comment.username || 'Unknown'}</strong>
                </small>
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