import {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {getToken} from '../services/authService';

const CreateBacaan = () => {
    const [form, setForm] = useState({judul: '', isiTeks: ''});
    const [error, setError] = useState('');
    const [saving, setSaving] = useState(false);
    const navigate = useNavigate();

    const handleSave = async (e) => {
        e.preventDefault();
        const token = getToken();

        if (!token) {
            navigate('/login');
            return;
        }

        setSaving(true);
        setError('');

        try {
            const response = await fetch('/api/bacaan', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(form),
            });

            if (response.status === 401 || response.status === 403) {
                setError('Sesi habis. Silakan login lagi.');
                navigate('/login');
                return;
            }

            if (!response.ok) {
                const text = await response.text();
                throw new Error(text || 'Gagal menyimpan bacaan');
            }

            navigate('/');
        } catch (err) {
            setError(err.message || 'Terjadi kesalahan saat menyimpan bacaan');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="page-container" style={{alignItems: 'center', justifyContent: 'center'}}>
            <div className="form-card">
                <Link to="/" style={{color: 'var(--blue)', textDecoration: 'none'}}>← Kembali</Link>
                <h2 style={{color: 'var(--lavender)', margin: '20px 0'}}>Tambah Bacaan Baru</h2>
                <form onSubmit={handleSave} style={{display: 'flex', flexDirection: 'column', gap: '20px'}}>
                    <input
                        className="input-entry"
                        placeholder="Judul"
                        style={{
                            width: '100%',
                            padding: '12px',
                            backgroundColor: 'var(--base)',
                            color: 'var(--text)',
                            border: '2px solid var(--surface1)',
                            borderRadius: '8px'
                        }}
                        value={form.judul}
                        onChange={(e) => setForm({...form, judul: e.target.value})}
                        required
                    />
                    <textarea
                        className="input-entry"
                        rows="8"
                        placeholder="Konten"
                        style={{
                            width: '100%',
                            padding: '12px',
                            backgroundColor: 'var(--base)',
                            color: 'var(--text)',
                            border: '2px solid var(--surface1)',
                            borderRadius: '8px'
                        }}
                        value={form.isiTeks}
                        onChange={(e) => setForm({...form, isiTeks: e.target.value})}
                        required
                    />
                    {error && <div className="status-error">{error}</div>}
                    <button type="submit" className="btn btn-edit" disabled={saving}>
                        {saving ? 'Menyimpan...' : 'Simpan'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default CreateBacaan;