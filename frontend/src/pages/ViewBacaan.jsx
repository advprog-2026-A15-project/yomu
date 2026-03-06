import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { logout, getToken } from "../services/authService";

export default function ViewBacaan() {
  const [bacaans, setBacaans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  useEffect(() => {
    const controller = new AbortController();
    const token = getToken();

    if (!token) {
      // kalau tidak ada token, arahkan ke login
      navigate("/login");
      return;
    }

    async function load() {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch("http://localhost:8080/api/bacaan", {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          signal: controller.signal
        });

        if (res.status === 401 || res.status === 403) {
          // token invalid atau expired -> logout dan redirect
          logout();
          navigate("/login");
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
          console.error("Load bacaan error:", err);
          setError(err.message || "Terjadi kesalahan saat memuat data");
        }
      } finally {
        setLoading(false);
      }
    }

    load();

    return () => controller.abort();
  }, [navigate]);

  return (
    <div className="page-container" style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 30 }}>
        <h1 style={{ color: 'var(--lavender)', margin: 0 }}>Yomu: Daftar Bacaan</h1>

        <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
          <Link to="/create">
            <button className="btn" style={{ backgroundColor: 'var(--green)', color: 'var(--base)' }}>+ Tambah</button>
          </Link>

          <button
            className="btn"
            onClick={handleLogout}
            style={{ backgroundColor: 'transparent', color: 'var(--muted)', border: '1px solid transparent', cursor: 'pointer' }}
            title="Logout"
          >
            Logout
          </button>
        </div>
      </div>

      {loading && <div style={{ padding: 12 }}>Memuat bacaan...</div>}
      {error && <div style={{ color: '#b00020', padding: 12 }}>{error}</div>}

      {!loading && !error && (
        <div style={{ overflowX: 'auto' }}>
          <table className="macchiato-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <th className="col-id">UUID</th>
                <th>Judul</th>
                <th>Konten</th>
                <th style={{ width: 180, textAlign: 'center' }}>Aksi</th>
              </tr>
            </thead>
            <tbody>
              {bacaans.length === 0 && (
                <tr>
                  <td colSpan={4} style={{ textAlign: 'center', padding: 16, color: 'var(--muted)' }}>
                    Belum ada bacaan.
                  </td>
                </tr>
              )}
              {bacaans.map(b => (
                <tr key={b.id}>
                  <td className="col-id" title={b.id} style={{ padding: '8px 12px', maxWidth: 120, overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {b.id}
                  </td>
                  <td title={b.judul} style={{ padding: '8px 12px' }}>
                    <strong>
                      {b.judul?.length > 40 ? b.judul.substring(0, 40) + "..." : b.judul}
                    </strong>
                  </td>
                  <td title={b.isiTeks} style={{ padding: '8px 12px' }}>
                    {b.isiTeks?.length > 80 ? b.isiTeks.substring(0, 80) + "..." : b.isiTeks}
                  </td>
                  <td style={{ textAlign: 'center', padding: '8px 12px' }}>
                    <Link to={`/edit/${b.id}`}>
                      <button className="btn" style={{ backgroundColor: 'var(--blue)', color: 'var(--base)', marginRight: 8 }}>Edit</button>
                    </Link>
                    <Link to={`/delete/${b.id}`}>
                      <button className="btn" style={{ backgroundColor: 'var(--red)', color: 'var(--base)' }}>Hapus</button>
                    </Link>
                  </td>
                </tr>
                </thead>
                <tbody>
                {bacaans.map(b => (
                    <tr key={b.id}>
                        <td className="col-id" title={b.id}>{b.id}</td>
                        <td title={b.judul}><strong>{b.judul.length > 40
                            ? b.judul.substring(0, 40) + "..."
                            : b.judul}</strong></td>
                        <td title={b.isiTeks}>{b.isiTeks.length > 80
                            ? b.isiTeks.substring(0, 80) + "..."
                            : b.isiTeks}</td>
                        <td style={{ textAlign: 'center', display: 'flex', gap: '8px', justifyContent: 'center', flexWrap: 'nowrap' }}>
                            <Link to={`/edit/${b.id}`}>
                                <button className="btn" style={{ backgroundColor: 'var(--blue)', color: 'var(--base)' }}>Edit</button>
                            </Link>
                            <Link to={`/delete/${b.id}`}>
                                <button className="btn" style={{ backgroundColor: 'var(--red)', color: 'var(--base)' }}>Hapus</button>
                            </Link>
                            <Link to={`/bacaan/${b.id}`}>
                                <button className="btn" style={{ backgroundColor: 'var(--lavender)', color: 'var(--base)' }}>Detail</button>
                            </Link>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
      )}
    </div>
  );
}