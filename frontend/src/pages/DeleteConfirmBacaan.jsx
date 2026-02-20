import { useEffect, useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';

export default function DeleteConfirmBacaan() {
    const { id } = useParams();
    const [judul, setJudul] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        fetch(`http://localhost:8080/api/bacaan/${id}`)
            .then(res => res.json())
            .then(data => setJudul(data.judul))
            .catch(() => setJudul("Data tidak ditemukan"));
    }, [id]);

    const handleDelete = () => {
        fetch(`http://localhost:8080/api/bacaan/${id}`, { method: 'DELETE' })
            .then(() => navigate('/'));
    };

    return (
        <div className="page-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
            <div className="form-card" style={{ textAlign: 'center', borderColor: 'var(--red)' }}>
                <h2 style={{ color: 'var(--red)' }}>⚠ Konfirmasi Hapus</h2>
                <p>Apakah Anda yakin ingin menghapus bacaan:</p>
                <p><strong style={{ color: 'var(--lavender)', fontSize: '20px' }}>"{judul}"</strong></p>
                <p style={{ fontSize: '12px', color: 'var(--subtext)' }}>ID: {id}</p>

                <div style={{ marginTop: '30px', display: 'flex', gap: '15px', justifyContent: 'center' }}>
                    <button className="btn" onClick={handleDelete} style={{ backgroundColor: 'var(--red)', color: 'var(--base)' }}>
                        Ya, Hapus
                    </button>
                    <Link to="/">
                        <button className="btn" style={{ backgroundColor: 'var(--surface1)', color: 'var(--text)' }}>
                            Batal
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    );
}