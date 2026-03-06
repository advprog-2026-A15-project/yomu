import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getToken, logout } from '../services/authService';

const API_BASE = 'http://localhost:8080';

export default function Achievements() {
  const navigate = useNavigate();
  const [achievements, setAchievements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    const controller = new AbortController();

    const loadAchievements = async () => {
      setLoading(true);
      setError('');
      try {
        const res = await fetch(`${API_BASE}/api/achievements/me`, {
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          signal: controller.signal,
        });

        if (res.status === 401 || res.status === 403) {
          logout();
          navigate('/login');
          return;
        }

        if (!res.ok) {
          const text = await res.text();
          throw new Error(text || 'Gagal memuat achievement');
        }

        const data = await res.json();
        setAchievements(Array.isArray(data) ? data : []);
      } catch (err) {
        if (err.name !== 'AbortError') {
          setError(err.message || 'Terjadi kesalahan saat memuat achievement');
        }
      } finally {
        setLoading(false);
      }
    };

    loadAchievements();
    return () => controller.abort();
  }, [navigate]);

  return (
    <div className="page-container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 className="page-title">Achievement Saya</h1>
        <Link to="/" style={{ color: 'var(--blue)', textDecoration: 'none' }}>Kembali ke Bacaan</Link>
      </div>

      {loading && <div className="status-note">Memuat achievement...</div>}
      {error && <div className="status-error">{error}</div>}

      {!loading && !error && (
        <section className="thread-list" aria-label="Daftar Achievement">
          {achievements.length === 0 ? (
            <div className="status-note">Belum ada achievement. Coba selesaikan kuis bacaan dulu.</div>
          ) : (
            achievements.map((achievement) => (
              <article className="thread-item" key={achievement.achievementId}>
                <div className="thread-content">
                  <h2 className="thread-title">{achievement.name}</h2>
                  <p className="thread-excerpt">{achievement.description}</p>
                  <div className="thread-meta">
                    Dicapai: {achievement.achievedAt ? new Date(achievement.achievedAt).toLocaleString() : '-'}
                  </div>
                </div>
              </article>
            ))
          )}
        </section>
      )}
    </div>
  );
}
