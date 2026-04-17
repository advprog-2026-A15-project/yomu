import { useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { getToken } from '../services/authService';

export default function DeleteConfirmKuis() {
  const { bacaanId, kuisId } = useParams(); // Ambil ID dari URL
  const navigate = useNavigate();
  const [isDeleting, setIsDeleting] = useState(false);
  const [error, setError] = useState('');

  const handleDelete = async () => {
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    setIsDeleting(true);
    setError('');

    try {
      // 👇 PASTIKAN URL-NYA TEPAT SEPERTI INI 👇
      const res = await fetch(`http://localhost:8080/api/kuis/${kuisId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (res.status === 401 || res.status === 403) {
        navigate('/login');
        return;
      }

      if (!res.ok) {
        throw new Error('Gagal menghapus kuis.');
      }

      // Jika berhasil, kembalikan ke halaman detail bacaan
      navigate(`/bacaan/${bacaanId}`);
    } catch (err) {
      setError(err.message || 'Terjadi kesalahan saat menghapus kuis.');
      setIsDeleting(false);
    }
  };

  return (
    <div className="page-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
      <div className="form-card" style={{ textAlign: 'center', borderColor: 'var(--red)' }}>
        <h2 style={{ color: 'var(--red)' }}>⚠ Konfirmasi Hapus Kuis</h2>
        <p>Apakah Anda yakin ingin menghapus soal kuis ini?</p>

        {error && <p style={{ color: 'var(--red)' }}>{error}</p>}

        <div style={{ marginTop: '30px', display: 'flex', gap: '15px', justifyContent: 'center' }}>
          <button
            className="btn"
            onClick={handleDelete}
            style={{ backgroundColor: 'var(--red)', color: 'var(--base)' }}
            disabled={isDeleting}
          >
            {isDeleting ? 'Menghapus...' : 'Ya, Hapus'}
          </button>

          <Link to={`/bacaan/${bacaanId}`}>
            <button className="btn" style={{ backgroundColor: 'var(--surface1)', color: 'var(--text)' }}>
              Batal
            </button>
          </Link>
        </div>
      </div>
    </div>
  );
}