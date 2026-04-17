import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getToken } from '../services/authService';

const EditBacaan = () => {
  const { id } = useParams();
  const [form, setForm] = useState({ judul: '', isiTeks: '', kategori: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

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
        const res = await fetch(`/api/bacaan/${id}`, {
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
          throw new Error(text || 'Gagal mengambil data bacaan');
        }

        const data = await res.json();
        setForm({
          judul: data?.judul || '',
          isiTeks: data?.isiTeks || '',
          kategori: data?.kategori || '',
        });
      } catch (err) {
        if (err.name !== 'AbortError') {
          setError(err.message || 'Terjadi kesalahan saat memuat data');
        }
      } finally {
        setLoading(false);
      }
    };

    load();
    return () => controller.abort();
  }, [id, navigate]);

  const handleUpdate = async (e) => {
    e.preventDefault();
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    setSaving(true);
    setError('');
    try {
      const res = await fetch(`/api/bacaan/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(form),
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
        throw new Error(text || 'Gagal memperbarui bacaan');
      }

      navigate('/');
    } catch (err) {
      setError(err.message || 'Terjadi kesalahan saat menyimpan perubahan');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="page-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
      <div className="form-card">
        <Link to="/" style={{ color: 'var(--blue)', textDecoration: 'none' }}>← Kembali</Link>
        <h2 style={{ color: 'var(--lavender)', margin: '20px 0' }}>Edit Bacaan</h2>

        {loading && <div className="status-note">Memuat data bacaan...</div>}
        {!loading && error && <div className="status-error">{error}</div>}

        {!loading && !error && (
          <form onSubmit={handleUpdate} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <input
              className="input-entry"
              placeholder="Judul"
              style={{ width: '100%', padding: '12px', backgroundColor: 'var(--base)', color: 'var(--text)', border: '2px solid var(--surface1)', borderRadius: '8px' }}
              value={form.judul}
              onChange={(e) => setForm({ ...form, judul: e.target.value })}
              required
            />
            <textarea
              className="input-entry"
              rows="8"
              placeholder="Konten"
              style={{ width: '100%', padding: '12px', backgroundColor: 'var(--base)', color: 'var(--text)', border: '2px solid var(--surface1)', borderRadius: '8px' }}
              value={form.isiTeks}
              onChange={(e) => setForm({ ...form, isiTeks: e.target.value })}
              required
            />
            <select
              className="input-entry"
              style={{ width: '100%', padding: '12px', backgroundColor: 'var(--base)', color: 'var(--text)', border: '2px solid var(--surface1)', borderRadius: '8px' }}
              value={form.kategori}
              onChange={(e) => setForm({ ...form, kategori: e.target.value })}
              required
            >
              <option value="" disabled>-- Pilih Kategori --</option>
              <option value="Edukasi">Edukasi</option>
              <option value="Sejarah">Sejarah</option>
              <option value="Sains">Sains</option>
            </select>
            <button type="submit" className="btn btn-edit" disabled={saving}>
              {saving ? 'Menyimpan...' : 'Simpan'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
};

export default EditBacaan;