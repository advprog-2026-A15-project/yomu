import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getCurrentUser, getRole, getToken, logout } from '../services/authService';

const API_BASE = '';

export default function Clans() {
  const LEAGUES = ['BRONZE', 'SILVER', 'GOLD'];
  const navigate = useNavigate();
  const [clans, setClans] = useState([]);
  const [selectedLeague, setSelectedLeague] = useState('BRONZE');
  const [leaderboardEntries, setLeaderboardEntries] = useState([]);
  const [myLeaderboardEntry, setMyLeaderboardEntry] = useState(null);
  const [isAdmin, setIsAdmin] = useState(getRole() === 'ADMIN');
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(true);
  const [leaderboardLoading, setLeaderboardLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [endingSeason, setEndingSeason] = useState(false);
  const [adminMessage, setAdminMessage] = useState('');
  const [error, setError] = useState('');
  const [leaderboardError, setLeaderboardError] = useState('');
  const [adminError, setAdminError] = useState('');

  const joinedClans = useMemo(
    () => clans.filter((clan) => clan.joined),
    [clans]
  );

  const loadClans = useCallback(async (signal) => {
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const res = await fetch(`${API_BASE}/api/clans`, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        signal,
      });

      if (res.status === 401 || res.status === 403) {
        logout();
        navigate('/login');
        return;
      }

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || 'Gagal memuat daftar clan');
      }

      const data = await res.json();
      setClans(Array.isArray(data) ? data : []);
    } catch (err) {
      if (err.name !== 'AbortError') {
        setError(err.message || 'Terjadi kesalahan saat memuat clan');
      }
    } finally {
      setLoading(false);
    }
  }, [navigate]);

  const loadLeaderboard = useCallback(async (signal, league = selectedLeague) => {
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    setLeaderboardLoading(true);
    setLeaderboardError('');
    try {
      const leaderboardRes = await fetch(`${API_BASE}/api/clans/leaderboard?league=${league}`, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        signal,
      });

      if (leaderboardRes.status === 401 || leaderboardRes.status === 403) {
        logout();
        navigate('/login');
        return;
      }

      if (!leaderboardRes.ok) {
        const text = await leaderboardRes.text();
        throw new Error(text || 'Gagal memuat leaderboard');
      }

      const leaderboardData = await leaderboardRes.json();
      setLeaderboardEntries(Array.isArray(leaderboardData) ? leaderboardData : []);

      const myRes = await fetch(`${API_BASE}/api/clans/leaderboard/me`, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        signal,
      });

      if (myRes.status === 401 || myRes.status === 403) {
        logout();
        navigate('/login');
        return;
      }

      if (myRes.status === 404) {
        setMyLeaderboardEntry(null);
      } else if (!myRes.ok) {
        const text = await myRes.text();
        throw new Error(text || 'Gagal memuat ranking clan saya');
      } else {
        const myData = await myRes.json();
        setMyLeaderboardEntry(myData || null);
      }
    } catch (err) {
      if (err.name !== 'AbortError') {
        setLeaderboardError(err.message || 'Terjadi kesalahan saat memuat leaderboard');
      }
    } finally {
      setLeaderboardLoading(false);
    }
  }, [navigate, selectedLeague]);

  const loadCurrentUserRole = useCallback(async (signal) => {
    const token = getToken();
    if (!token) {
      return;
    }

    try {
      const user = await getCurrentUser();
      if (signal?.aborted) return;
      setIsAdmin(user?.role === 'ADMIN' || getRole() === 'ADMIN');
    } catch {
      if (signal?.aborted) return;
      setIsAdmin(getRole() === 'ADMIN');
    }
  }, []);

  useEffect(() => {
    const controller = new AbortController();
    loadClans(controller.signal);
    loadLeaderboard(controller.signal, selectedLeague);
    loadCurrentUserRole(controller.signal);
    return () => controller.abort();
  }, [loadClans, loadLeaderboard, loadCurrentUserRole, selectedLeague]);

  const handleCreateClan = async (e) => {
    e.preventDefault();
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    setSubmitting(true);
    setError('');
    try {
      const res = await fetch(`${API_BASE}/api/clans`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ name, description }),
      });

      const text = await res.text();
      if (!res.ok) {
        throw new Error(text || 'Gagal membuat clan');
      }

      setName('');
      setDescription('');
      await loadClans();
    } catch (err) {
      setError(err.message || 'Terjadi kesalahan saat membuat clan');
    } finally {
      setSubmitting(false);
    }
  };

  const handleJoinClan = async (clanId) => {
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    setSubmitting(true);
    setError('');
    try {
      const res = await fetch(`${API_BASE}/api/clans/${clanId}/join`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });

      const text = await res.text();
      if (!res.ok) {
        throw new Error(text || 'Gagal bergabung ke clan');
      }

      await loadClans();
    } catch (err) {
      setError(err.message || 'Terjadi kesalahan saat join clan');
    } finally {
      setSubmitting(false);
    }
  };

  const handleEndSeason = async () => {
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    setEndingSeason(true);
    setAdminError('');
    setAdminMessage('');
    try {
      const res = await fetch(`${API_BASE}/api/admin/clans/end-season`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });

      if (res.status === 401 || res.status === 403) {
        if (res.status === 401) {
          logout();
          navigate('/login');
          return;
        }
        throw new Error('Akses ditolak. Hanya admin yang bisa mengakhiri season.');
      }

      const text = await res.text();
      if (!res.ok) {
        throw new Error(text || 'Gagal mengakhiri season');
      }

      setAdminMessage('Season berhasil diproses. Leaderboard telah diperbarui.');
      await Promise.all([loadClans(), loadLeaderboard(undefined, selectedLeague)]);
    } catch (err) {
      setAdminError(err.message || 'Terjadi kesalahan saat memproses end season');
    } finally {
      setEndingSeason(false);
    }
  };

  return (
    <div className="page-container" style={{ gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 className="page-title">Clan & Liga</h1>
        <Link to="/" style={{ color: 'var(--blue)', textDecoration: 'none' }}>Kembali ke Bacaan</Link>
      </div>

      <section className="form-card" style={{ maxWidth: 'none' }}>
        <h2 style={{ marginTop: 0, color: 'var(--lavender)' }}>Buat Clan Baru</h2>
        <form onSubmit={handleCreateClan} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <input
            className="input-entry"
            placeholder="Nama clan"
            value={name}
            onChange={(e) => setName(e.target.value)}
            minLength={3}
            maxLength={100}
            required
          />
          <textarea
            className="input-entry"
            placeholder="Deskripsi clan (opsional)"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
            maxLength={255}
          />
          <button className="btn btn-add" type="submit" disabled={submitting}>
            {submitting ? 'Memproses...' : 'Buat Clan'}
          </button>
        </form>
      </section>

      {error && <div className="status-error">{error}</div>}
      {loading && <div className="status-note">Memuat clan...</div>}

      <section className="form-card" style={{ maxWidth: 'none' }}>
        <h2 style={{ marginTop: 0, color: 'var(--lavender)' }}>Leaderboard Liga</h2>
        <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginBottom: '12px' }}>
          {LEAGUES.map((league) => (
            <button
              key={league}
              className={`btn ${selectedLeague === league ? 'btn-detail' : 'btn-ghost'}`}
              type="button"
              onClick={() => setSelectedLeague(league)}
              disabled={leaderboardLoading}
            >
              {league}
            </button>
          ))}
        </div>

        {leaderboardError && <div className="status-error">{leaderboardError}</div>}
        {leaderboardLoading && <div className="status-note">Memuat leaderboard...</div>}

        {!leaderboardLoading && (
          <>
            {myLeaderboardEntry ? (
              <div className="status-note" style={{ marginBottom: '12px' }}>
                Clan saya: <strong>{myLeaderboardEntry.clanName}</strong> | Liga:{' '}
                <strong>{myLeaderboardEntry.league}</strong> | Rank: <strong>#{myLeaderboardEntry.rank}</strong> |
                Poin: <strong>{myLeaderboardEntry.currentSeasonPoints}</strong>
              </div>
            ) : (
              <div className="status-note" style={{ marginBottom: '12px' }}>
                Anda belum memiliki peringkat clan.
              </div>
            )}

            {leaderboardEntries.length === 0 ? (
              <div className="status-note">Belum ada data leaderboard di liga ini.</div>
            ) : (
              <ol style={{ margin: 0, paddingLeft: '20px' }}>
                {leaderboardEntries.map((entry) => (
                  <li key={entry.clanId} style={{ marginBottom: '8px' }}>
                    <strong>{entry.clanName}</strong> - Rank #{entry.rank} - {entry.currentSeasonPoints} poin
                  </li>
                ))}
              </ol>
            )}
          </>
        )}
      </section>

      {isAdmin && (
        <section className="form-card" style={{ maxWidth: 'none' }}>
          <h2 style={{ marginTop: 0, color: 'var(--lavender)' }}>Admin: End Season</h2>
          <p style={{ marginTop: 0 }}>
            Tombol ini akan memicu kalkulasi promosi/degradasi berdasarkan skor clan saat ini.
          </p>
          {adminError && <div className="status-error">{adminError}</div>}
          {adminMessage && <div className="status-note">{adminMessage}</div>}
          <button className="btn btn-delete" type="button" onClick={handleEndSeason} disabled={endingSeason}>
            {endingSeason ? 'Memproses end season...' : 'End Season Sekarang'}
          </button>
        </section>
      )}

      {!loading && (
        <>
          <section className="form-card" style={{ maxWidth: 'none' }}>
            <h2 style={{ marginTop: 0, color: 'var(--lavender)' }}>Clan Saya ({joinedClans.length})</h2>
            {joinedClans.length === 0 ? (
              <p>Anda belum bergabung ke clan mana pun.</p>
            ) : (
              <ul style={{ margin: 0, paddingLeft: '18px' }}>
                {joinedClans.map((clan) => (
                  <li key={clan.id}>
                    {clan.name} ({clan.memberCount} anggota)
                  </li>
                ))}
              </ul>
            )}
          </section>

          <section className="thread-list" aria-label="Daftar Clan">
            {clans.length === 0 ? (
              <div className="status-note">Belum ada clan. Jadilah yang pertama membuat.</div>
            ) : (
              clans.map((clan) => (
                <article className="thread-item" key={clan.id}>
                  <div className="thread-content">
                    <h3 className="thread-title">{clan.name}</h3>
                    <p className="thread-excerpt">{clan.description || 'Tanpa deskripsi.'}</p>
                    <div className="thread-meta">
                      Owner: {clan.ownerUsername} | Anggota: {clan.memberCount}
                    </div>
                  </div>
                  <div className="thread-actions">
                    {clan.joined ? (
                      <button className="btn btn-detail" type="button" disabled>Sudah Bergabung</button>
                    ) : (
                      <button
                        className="btn btn-add"
                        type="button"
                        onClick={() => handleJoinClan(clan.id)}
                        disabled={submitting}
                      >
                        Gabung
                      </button>
                    )}
                  </div>
                </article>
              ))
            )}
          </section>
        </>
      )}
    </div>
  );
}
