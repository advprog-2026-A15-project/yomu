import {useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';

export default function DetailBacaanKuis() {
    const {id} = useParams();
    const [bacaan, setBacaan] = useState(null);
    const [jawaban, setJawaban] = useState('');
    const [hasilKuis, setHasilKuis] = useState(null);

    // Ambil data teks bacaan
    useEffect(() => {
        fetch(`http://localhost:8080/api/bacaan/${id}`)
            .then(res => res.json())
            .then(data => setBacaan(data))
            .catch(err => console.error("Gagal mengambil bacaan:", err));
    }, [id]);

    // Fungsi kirim jawaban kuis
    const handleSubmitKuis = (e) => {
        e.preventDefault();
        fetch(`http://localhost:8080/api/bacaan/${id}/kuis/submit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain' // Karena backend kita nerima String biasa
            },
            body: jawaban
        })
            .then(res => res.text()) // Backend membalas dengan text "Benar!" / "Salah!"
            .then(text => {
                setHasilKuis(text);
                setJawaban(''); // Kosongkan input setelah submit
            })
            .catch(err => console.error("Gagal submit kuis:", err));
    };

    if (!bacaan) return <div style={{color: 'var(--text)', textAlign: 'center', marginTop: '50px'}}>Loading
        teks...</div>;

    return (
        <div className="page-container" style={{alignItems: 'center', padding: '20px'}}>
            <div className="form-card" style={{maxWidth: '800px', width: '100%'}}>
                <Link to="/" style={{color: 'var(--blue)', textDecoration: 'none'}}>← Kembali ke Daftar</Link>

                {/* Bagian Teks Bacaan */}
                <h1 style={{color: 'var(--lavender)', marginTop: '20px'}}>{bacaan.judul}</h1>
                <div style={{
                    backgroundColor: 'var(--base)',
                    padding: '20px',
                    borderRadius: '8px',
                    border: '1px solid var(--surface1)',
                    marginTop: '20px',
                    lineHeight: '1.6',
                    color: 'var(--text)'
                }}>
                    {bacaan.isiTeks}
                </div>

                {/* Bagian Kuis POC */}
                <div style={{
                    marginTop: '40px',
                    padding: '20px',
                    backgroundColor: 'var(--surface0)',
                    borderRadius: '8px'
                }}>
                    <h3 style={{color: 'var(--peach, orange)', marginBottom: '15px'}}>Kuis Pemahaman</h3>
                    <p style={{color: 'var(--text)', fontSize: '18px', marginBottom: '15px'}}>
                        {/* Mengambil elemen kuis pertama (indeks 0) dari array quizzes */}
                        {bacaan.quizzes?.[0]?.pertanyaan || "⚠️ Pertanyaan belum terkirim dari Backend API"}
                    </p>

                    {hasilKuis && (
                        <div style={{
                            padding: '10px',
                            marginBottom: '15px',
                            borderRadius: '5px',
                            backgroundColor: hasilKuis.includes("Benar") ? 'var(--green)' : 'var(--red)',
                            color: 'var(--base)',
                            fontWeight: 'bold',
                            textAlign: 'center'
                        }}>
                            {hasilKuis}
                        </div>
                    )}

                    <form onSubmit={handleSubmitKuis} style={{display: 'flex', gap: '10px'}}>
                        <input
                            type="text"
                            className="input-entry"
                            placeholder="Ketik jawaban Anda di sini (Hint: cari kata 'hoax')"
                            style={{
                                flex: 1,
                                padding: '12px',
                                borderRadius: '8px',
                                border: '1px solid var(--surface1)',
                                backgroundColor: 'var(--base)',
                                color: 'var(--text)'
                            }}
                            value={jawaban}
                            onChange={e => setJawaban(e.target.value)}
                            required
                        />
                        <button type="submit" className="btn"
                                style={{backgroundColor: 'var(--blue)', color: 'var(--base)', padding: '0 20px'}}>
                            Kirim Jawaban
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}