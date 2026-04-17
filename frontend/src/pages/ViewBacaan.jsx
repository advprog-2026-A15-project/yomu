import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { logout, getToken } from '../services/authService';

const truncate = (value, maxLength) => {
  if (!value) return '-';
  return value.length > maxLength ? `${value.slice(0, maxLength)}...` : value;
};

export default function ViewBacaan() {
  const [bacaans, setBacaans] = useState([]);
  const [kategoriTerpilih, setKategoriTerpilih] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  useEffect(() => {
    const controller = new AbortController();
    const token = getToken();

    if (!token) {
      navigate('/login');
      return;
    }

    async function load() {
      setLoading(true);
      setError(null);

      try {
          let url = 'http://localhost:8080/api/bacaan';
            if (kategoriTerpilih) {
              url += `?kategori=${kategoriTerpilih}`;
            }

          const res = await fetch(url, {
            method: 'GET',
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
          throw new Error(text || 'Gagal memuat bacaan');
        }

        const data = await res.json();
        setBacaans(Array.isArray(data) ? data : []);
      } catch (err) {
        if (err.name !== 'AbortError') {
          console.error('Load bacaan error:', err);
          setError(err.message || 'Terjadi kesalahan saat memuat data');
        }
      } finally {
        setLoading(false);
      }
    }

    load();
    return () => controller.abort();
  }, [kategoriTerpilih]);;

  return (
    <div className="page-container">
      <header className="page-header">
        <h1 className="page-title">Yomu: Daftar Bacaan</h1>
        <div className="header-actions">
          <Link to="/achievements">
            <button className="btn btn-detail" type="button">Achievement</button>
          </Link>
          <Link to="/clans">
            <button className="btn btn-edit" type="button">Clan</button>
          </Link>
          <Link to="/create">
            <button className="btn btn-add" type="button">+ Tambah</button>
          </Link>
          <button className="btn btn-ghost" onClick={handleLogout} type="button" title="Logout">
            Logout
          </button>
        </div>
      </header>

      {loading && <div className="status-note">Memuat bacaan...</div>}
      {error && <div className="status-error">{error}</div>}

      {!loading && !error && (
        <section className="thread-list" aria-label="Daftar Bacaan">
        <div style={{ marginBottom: '20px', display: 'flex', gap: '10px', alignItems: 'center' }}>
          <label style={{ color: 'var(--text)' }}>Filter Kategori:</label>
          <select
            value={kategoriTerpilih}
            onChange={(e) => setKategoriTerpilih(e.target.value)}
            style={{ padding: '8px', borderRadius: '5px', backgroundColor: 'var(--surface0)', color: 'var(--text)' }}
          >
            <option value="">Semua Kategori</option>
            <option value="Edukasi">Edukasi</option>
            <option value="Sejarah">Sejarah</option>
            <option value="Sains">Sains</option>
          </select>
        </div>
          {bacaans.length === 0 && <div className="status-note">Belum ada bacaan.</div>}

          {bacaans.map((b) => (
            <article className="thread-item" key={b.id}>
              <div className="thread-content">
                <div className="thread-meta" title={b.id}>
                  UUID: {b.id}
                </div>
                {b.kategori && (
                    <span style={{ backgroundColor: 'var(--blue)', color: 'var(--base)', padding: '2px 8px', borderRadius: '12px', fontSize: '12px', fontWeight: 'bold' }}>
                      {b.kategori}
                    </span>
                )}
                <h2 className="thread-title" title={b.judul || '-'}>
                  {truncate(b.judul, 72)}
                </h2>
                <p className="thread-excerpt" title={b.isiTeks || '-'}>
                  {truncate(b.isiTeks, 180)}
                </p>
              </div>

              <div className="thread-actions">
                <Link to={`/bacaan/${b.id}`}>
                  <button className="btn btn-detail" type="button">Detail</button>
                </Link>
                <Link to={`/edit/${b.id}`}>
                  <button className="btn btn-edit" type="button">Edit</button>
                </Link>
                <Link to={`/delete/${b.id}`}>
                  <button className="btn btn-delete" type="button">Hapus</button>
                </Link>
              </div>
            </article>
          ))}
        </section>
      )}
    </div>
  );
}
