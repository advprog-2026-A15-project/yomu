import {useEffect, useMemo, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {getToken, logout} from '../services/authService';

const API_BASE = 'http://localhost:8080';

export default function Clans() {
    const navigate = useNavigate();
    const [clans, setClans] = useState([]);
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    const joinedClans = useMemo(
        () => clans.filter((clan) => clan.joined),
        [clans]
    );

    const loadClans = async (signal) => {
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
    };

    useEffect(() => {
        const controller = new AbortController();
        loadClans(controller.signal);
        return () => controller.abort();
    }, [navigate]);

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
                body: JSON.stringify({name, description}),
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

    return (
        <div className="page-container" style={{gap: '24px'}}>
            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                <h1 className="page-title">Clan & Liga</h1>
                <Link to="/" style={{color: 'var(--blue)', textDecoration: 'none'}}>Kembali ke Bacaan</Link>
            </div>

            <section className="form-card" style={{maxWidth: 'none'}}>
                <h2 style={{marginTop: 0, color: 'var(--lavender)'}}>Buat Clan Baru</h2>
                <form onSubmit={handleCreateClan} style={{display: 'flex', flexDirection: 'column', gap: '12px'}}>
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

            {!loading && (
                <>
                    <section className="form-card" style={{maxWidth: 'none'}}>
                        <h2 style={{marginTop: 0, color: 'var(--lavender)'}}>Clan Saya ({joinedClans.length})</h2>
                        {joinedClans.length === 0 ? (
                            <p>Anda belum bergabung ke clan mana pun.</p>
                        ) : (
                            <ul style={{margin: 0, paddingLeft: '18px'}}>
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
                                            <button className="btn btn-detail" type="button" disabled>Sudah
                                                Bergabung</button>
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
