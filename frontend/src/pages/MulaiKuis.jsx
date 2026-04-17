import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getToken } from '../services/authService';

const API_BASE = 'http://localhost:8080';

export default function MulaiKuis() {
  const { id } = useParams(); // ID Bacaan
  const navigate = useNavigate();

  const [bacaan, setBacaan] = useState(null);
  const [currentIndex, setCurrentIndex] = useState(0);

  // State untuk menyimpan jawaban sementara dan hasil cek
  const [userAnswers, setUserAnswers] = useState({});
  const [checkResults, setCheckResults] = useState({});

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Layar Skor
  const [isFinished, setIsFinished] = useState(false);
  const [finalScore, setFinalScore] = useState(0);

  useEffect(() => {
    const loadKuis = async () => {
      const token = getToken();
      if (!token) return navigate('/login');

      try {
        const res = await fetch(`${API_BASE}/api/bacaan/${id}`, {
          headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` }
        });

        if (!res.ok) throw new Error('Gagal memuat kuis (Mungkin Anda sudah menyelesaikannya)');
        setBacaan(await res.json());
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    loadKuis();
  }, [id, navigate]);

  // Handle Input (Bebas edit kapan saja)
  const handleInputChange = (e) => {
    const val = e.target.value;
    setUserAnswers(prev => ({ ...prev, [currentIndex]: val }));
    setCheckResults(prev => ({ ...prev, [currentIndex]: null })); // Reset status benar/salah saat diedit
  };

  // Handle Cek Jawaban Per Soal
  const handleCheckAnswer = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const res = await fetch(`${API_BASE}/api/kuis/${bacaan.quizzes[currentIndex].id}/submit`, {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain', Authorization: `Bearer ${getToken()}` },
        body: userAnswers[currentIndex] || '',
      });
      const text = await res.text();
      setCheckResults(prev => ({ ...prev, [currentIndex]: text }));
    } finally {
      setIsSubmitting(false);
    }
  };

  // 👇 INI YANG AKAN MENGUNCI KUIS PERMANEN 👇
  const handleSelesaikanKuis = async () => {
    setIsSubmitting(true);

    // Hitung skor
    let benarCount = 0;
    Object.values(checkResults).forEach(res => {
      if (res && res.includes('Benar')) benarCount++;
    });
    setFinalScore(benarCount);

    try {
      // Tembak endpoint untuk menyimpan history dan memicu gembok larangan
      await fetch(`${API_BASE}/api/kuis/bacaan/${id}/finish`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setIsFinished(true); // Tampilkan layar skor
    } catch (err) {
      alert("Gagal mengunci kuis.");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) return <div className="page-container"><p>Memuat Kuis...</p></div>;
  if (error) return <div className="page-container"><p style={{color: 'var(--red)'}}>{error}</p><Link to="/">Kembali ke Home</Link></div>;
  if (!bacaan || !bacaan.quizzes || bacaan.quizzes.length === 0) return <div className="page-container"><p>Belum ada soal.</p></div>;

  // --- LAYAR SKOR AKHIR ---
  if (isFinished) {
    return (
      <div className="page-container" style={{ alignItems: 'center' }}>
        <div className="form-card" style={{ width: '100%', maxWidth: '500px', textAlign: 'center' }}>
          <h2 style={{ color: 'var(--lavender)' }}>Kuis Selesai! 🎉</h2>
          <p style={{ color: 'var(--subtext0)', fontSize: '14px' }}>Kuis ini telah dikunci. Sesuai aturan, Anda tidak dapat membaca kuis ini lagi.</p>

          <div style={{ margin: '30px 0', padding: '20px', backgroundColor: 'var(--base)', borderRadius: '12px', border: '2px solid var(--surface1)' }}>
            <p style={{ fontSize: '18px', margin: '0 0 10px 0', color: 'var(--subtext0)' }}>Skor Jawaban Benar</p>
            <h1 style={{ color: 'var(--green)', fontSize: '48px', margin: 0 }}>{finalScore} / {bacaan.quizzes.length}</h1>
          </div>

          <Link to="/">
            <button className="btn" style={{ backgroundColor: 'var(--blue)', color: 'var(--base)', width: '100%', padding: '12px' }}>
              Kembali ke Beranda
            </button>
          </Link>
        </div>
      </div>
    );
  }

  // --- LAYAR PENGERJAAN KUIS ---
  const isLastQuestion = currentIndex === bacaan.quizzes.length - 1;
  const isFirstQuestion = currentIndex === 0;
  const currentResult = checkResults[currentIndex] || '';
  const isAnswerCorrect = currentResult.includes('Benar');

  return (
    <div className="page-container" style={{ alignItems: 'center' }}>
      <div className="form-card" style={{ width: '100%', maxWidth: '700px' }}>

        {/* Header: Tombol Keluar Sementara (TIDAK MENGUNCI KUIS) */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '1px solid var(--surface1)', paddingBottom: '15px' }}>
          <Link to={`/bacaan/${id}`} style={{ color: 'var(--red)', textDecoration: 'none', fontWeight: 'bold' }}>
            ✖ Keluar (Simpan Nanti)
          </Link>
          <span style={{ backgroundColor: 'var(--surface1)', padding: '6px 14px', borderRadius: '20px', fontWeight: 'bold' }}>
            Soal {currentIndex + 1} dari {bacaan.quizzes.length}
          </span>
        </div>

        {/* Kotak Pertanyaan */}
        <div style={{ backgroundColor: 'var(--base)', padding: '25px', borderRadius: '12px', marginBottom: '20px', minHeight: '120px' }}>
          <p style={{ fontSize: '20px', margin: 0, lineHeight: 1.6 }}>{bacaan.quizzes[currentIndex].pertanyaan}</p>
        </div>

        {/* Form Jawaban */}
        <form onSubmit={handleCheckAnswer} style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginBottom: '20px' }}>
          <input
            type="text"
            className="input-entry"
            placeholder="Ketik jawabanmu..."
            value={userAnswers[currentIndex] || ''}
            onChange={handleInputChange}
            required
            style={{ padding: '15px', fontSize: '16px' }}
          />
          <button className="btn" type="submit" disabled={isSubmitting || isAnswerCorrect}
            style={{
              backgroundColor: isAnswerCorrect ? 'var(--surface1)' : 'var(--blue)',
              color: isAnswerCorrect ? 'var(--subtext0)' : 'var(--base)',
              padding: '12px', fontSize: '16px', cursor: isAnswerCorrect ? 'not-allowed' : 'pointer'
            }}>
            {isSubmitting ? 'Memeriksa...' : (isAnswerCorrect ? '✓ Jawaban Tersimpan' : 'Cek Jawaban')}
          </button>
        </form>

        {/* Notifikasi Benar/Salah */}
        {currentResult && (
          <div style={{
            padding: '15px', borderRadius: '8px', textAlign: 'center', fontWeight: 'bold', marginBottom: '20px',
            backgroundColor: isAnswerCorrect ? 'rgba(166, 227, 161, 0.2)' : 'rgba(243, 139, 168, 0.2)',
            color: isAnswerCorrect ? 'var(--green)' : 'var(--red)'
          }}>{currentResult}</div>
        )}

        {/* Navigasi & Tombol Selesai */}
        <div style={{ display: 'flex', justifyContent: 'space-between', borderTop: '1px solid var(--surface1)', paddingTop: '20px' }}>
          <button className="btn" onClick={() => setCurrentIndex(p => p - 1)} disabled={isFirstQuestion} style={{ backgroundColor: 'var(--surface1)', color: 'var(--text)', opacity: isFirstQuestion ? 0.5 : 1 }}>
            ⬅ Prev
          </button>

          {!isLastQuestion ? (
            <button className="btn" onClick={() => setCurrentIndex(p => p + 1)} style={{ backgroundColor: 'var(--surface1)', color: 'var(--text)' }}>
              Next ➡
            </button>
          ) : (
            <button className="btn" onClick={handleSelesaikanKuis} disabled={isSubmitting} style={{ backgroundColor: 'var(--green)', color: 'var(--base)' }}>
              {isSubmitting ? 'Mengunci...' : 'Selesaikan Kuis 🎉'}
            </button>
          )}
        </div>

      </div>
    </div>
  );
}