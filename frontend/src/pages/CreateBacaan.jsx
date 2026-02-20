import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

const CreateBacaan = () => {
    const [form, setForm] = useState({ judul: '', isiTeks: '' });
    const navigate = useNavigate();

    const handleSave = (e) => {
        e.preventDefault();
        fetch("http://localhost:8080/api/bacaan", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(form)
        }).then(() => navigate('/'));
    };

    return (
        <div className="page-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
            <div className="form-card">
                <Link to="/" style={{ color: 'var(--blue)', textDecoration: 'none' }}>← Kembali</Link>
                <h2 style={{ color: 'var(--lavender)', margin: '20px 0' }}>Tambah Bacaan Baru</h2>
                <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    <input
                        className="input-entry"
                        placeholder="Judul"
                        style={{ width: '100%', padding: '12px', backgroundColor: 'var(--base)', color: 'var(--text)', border: '2px solid var(--surface1)', borderRadius: '8px' }}
                        value={form.judul}
                        onChange={e => setForm({...form, judul: e.target.value})}
                        required
                    />
                    <textarea
                        className="input-entry"
                        rows="8"
                        placeholder="Konten"
                        style={{ width: '100%', padding: '12px', backgroundColor: 'var(--base)', color: 'var(--text)', border: '2px solid var(--surface1)', borderRadius: '8px' }}
                        value={form.isiTeks}
                        onChange={e => setForm({...form, isiTeks: e.target.value})}
                        required
                    />
                    <button type="submit" className="btn" style={{ backgroundColor: 'var(--blue)', color: 'var(--base)' }}>
                        Simpan
                    </button>
                </form>
            </div>
        </div>
    );
};

export default CreateBacaan;