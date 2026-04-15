import {useEffect, useMemo, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {getCurrentUser, getToken} from '../services/authService';
import CommentItem from '../components/CommentItem';

const API_BASE = 'http://localhost:8080';

export default function DetailBacaan() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [bacaan, setBacaan] = useState(null);
  const [comments, setComments] = useState([]);
  const [jawaban, setJawaban] = useState('');
  const [hasilKuis, setHasilKuis] = useState('');
  const [isSubmittingQuiz, setIsSubmittingQuiz] = useState(false);
  const [myAchievements, setMyAchievements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const unlockedAchievements = myAchievements.filter((achievement) => achievement?.unlocked);

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
        const headers = {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        };

        const [bacaanRes, commentRes] = await Promise.all([
          fetch(`${API_BASE}/api/bacaan/${id}`, { headers, signal: controller.signal }),
          fetch(`${API_BASE}/api/comment`, { headers, signal: controller.signal }),
        ]);

        if (
          bacaanRes.status === 401 ||
          bacaanRes.status === 403 ||
          commentRes.status === 401 ||
          commentRes.status === 403
        ) {
          navigate('/login');
          return;
        }

        if (bacaanRes.status === 404) {
          setError('Bacaan tidak ditemukan.');
          return;
        }

        if (!bacaanRes.ok) {
          const text = await bacaanRes.text();
          throw new Error(text || 'Gagal mengambil detail bacaan');
        }

        if (!commentRes.ok) {
          const text = await commentRes.text();
          throw new Error(text || 'Gagal mengambil daftar komentar');
        }

        const bacaanData = await bacaanRes.json();
        const commentData = await commentRes.json();

        setBacaan(bacaanData);
        setComments(Array.isArray(commentData) ? commentData : []);

        try {
            const res = await fetch(`${API_BASE}/api/comment`, {
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                }
            });
            if (res.ok) {
                const data = await res.json();
                setComments(Array.isArray(data) ? data : []);
            }
        } catch (err) {
            console.error("Gagal refresh komentar", err);
        }
    };

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
                const headers = {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                };

                const [bacaanRes, commentRes] = await Promise.all([
                    fetch(`${API_BASE}/api/bacaan/${id}`, {headers, signal: controller.signal}),
                    fetch(`${API_BASE}/api/comment`, {headers, signal: controller.signal}),
                ]);

                if (
                    bacaanRes.status === 401 ||
                    bacaanRes.status === 403 ||
                    commentRes.status === 401 ||
                    commentRes.status === 403
                ) {
                    navigate('/login');
                    return;
                }

                if (bacaanRes.status === 404) {
                    setError('Bacaan tidak ditemukan.');
                    return;
                }

                if (!bacaanRes.ok) {
                    const text = await bacaanRes.text();
                    setError(text || 'Gagal mengambil detail bacaan');
                    return;
                }

                if (!commentRes.ok) {
                    const text = await commentRes.text();
                    setError(text || 'Gagal mengambil daftar komentar');
                    return;
                }

                const bacaanData = await bacaanRes.json();
                const commentData = await commentRes.json();

                setBacaan(bacaanData);
                setComments(Array.isArray(commentData) ? commentData : []);

                try {
                    const achievementRes = await fetch(`${API_BASE}/api/achievements/me`, {
                        headers,
                        signal: controller.signal
                    });
                    if (achievementRes.ok) {
                        const achievementData = await achievementRes.json();
                        setMyAchievements(Array.isArray(achievementData) ? achievementData : []);
                    }
                } catch {
                    // Achievement tidak kritikal untuk menampilkan halaman detail bacaan.
                }
            } catch (err) {
                if (err.name !== 'AbortError') {
                    setError(err.message || 'Terjadi kesalahan saat memuat data.');
                }
            } finally {
                setLoading(false);
            }
        };

        load();
        return () => controller.abort();
    }, [id, navigate]);

    useEffect(() => {
        let isMounted = true;

        getCurrentUser().then((user) => {
            if (isMounted) {
                setCurrentUser(user);
            }
        });

        return () => {
            isMounted = false;
        };
    }, []);

    const handleSubmitKuis = async (e) => {
        e.preventDefault();

        const token = getToken();
        if (!token) {
            navigate('/login');
            return;
        }

        setIsSubmittingQuiz(true);
        setHasilKuis('');
        try {
            const res = await fetch(`${API_BASE}/api/bacaan/${id}/kuis/submit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'text/plain',
                    Authorization: `Bearer ${token}`,
                },
                body: jawaban,
            });

            if (res.status === 401 || res.status === 403) {
                navigate('/login');
                return;
            }

            const text = await res.text();
            if (!res.ok) {
                setHasilKuis(text || 'Gagal submit kuis');
                return;
            }

            setHasilKuis(text);
            setJawaban('');

            const achievementRes = await fetch(`${API_BASE}/api/achievements/me`, {
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
            });

            if (achievementRes.ok) {
                const achievementData = await achievementRes.json();
                setMyAchievements(Array.isArray(achievementData) ? achievementData : []);
            }
        } catch (err) {
            setHasilKuis(err.message || 'Terjadi kesalahan saat submit kuis.');
        } finally {
            setIsSubmittingQuiz(false);
        }
    };

    const relatedComments = useMemo(() => {
        return comments.filter((comment) => {
            const isForThisBacaan = comment?.bacaan?.id === id || comment?.bacaanId === id;
            // Anggap backend menambahkan field parentId. Root comment adalah comment yang parentId-nya null/undefined
            const isRootComment = !comment.parentId;
            return isForThisBacaan && isRootComment;
        });
    }, [comments, id]);


    // Handle Submit Komentar Utama (Root)
    const handleMainCommentSubmit = async (e) => {
        e.preventDefault();
        const token = getToken();
        if (!token) {
            navigate('/login');
            return;
        }

        setIsSubmittingComment(true);
        try {
            const payload = {
                isiKomentar: newComment,
                bacaanId: id
                // parentCommentId dibiarkan kosong karena ini adalah root comment
            };

            const res = await fetch(`${API_BASE}/api/comment`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(payload),
            });

            if (res.ok) {
                setNewComment("");
                fetchCommentsOnly(); // Refresh daftar komentar
            } else {
                alert("Gagal mengirim komentar");
                return;
            }
        } catch (err) {
            alert(err.message);
        } finally {
            setIsSubmittingComment(false);
        }
    };

    if (loading) {
        return (
            <div className="page-container">
                <p>Memuat detail bacaan...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="page-container">
                <Link to="/" style={{color: 'var(--blue)', textDecoration: 'none'}}>← Kembali</Link>
                <p style={{color: 'var(--red)'}}>{error}</p>
            </div>
        );
    }

    if (!bacaan) {
        return (
            <div className="page-container">
                <Link to="/" style={{color: 'var(--blue)', textDecoration: 'none'}}>← Kembali</Link>
                <p>Bacaan tidak ditemukan.</p>
            </div>
        );
    }

    return (
        <div className="page-container" style={{gap: '24px'}}>
            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                <Link to="/" style={{color: 'var(--blue)', textDecoration: 'none'}}>← Kembali</Link>
                <Link to={`/bacaan/${id}/comment/new`}>
                    <button className="btn btn-add" type="button">+ Tambah Komentar</button>
                </Link>
            </div>

            <section className="form-card" style={{maxWidth: 'none'}}>
                <h2 style={{color: 'var(--lavender)', marginTop: 0}}>{bacaan.judul}</h2>
                <p style={{color: 'var(--subtext0)', marginBottom: '16px', wordBreak: 'break-all'}}>ID: {bacaan.id}</p>
                <p style={{whiteSpace: 'pre-wrap', lineHeight: 1.6}}>{bacaan.isiTeks}</p>
            </section>

             <section className="form-card" style={{ maxWidth: 'none' }}>
        <h3 style={{ color: 'var(--lavender)', marginTop: 0 }}>Kuis Pemahaman</h3>
        <p style={{ marginTop: 0 }}>
          {bacaan.quizzes?.[0]?.pertanyaan || 'Belum ada kuis untuk bacaan ini.'}
        </p>

        {bacaan.quizzes?.[0] ? (
          <form onSubmit={handleSubmitKuis} style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
            <input
              type="text"
              className="input-entry"
              placeholder="Masukkan jawaban Anda"
              value={jawaban}
              onChange={(e) => setJawaban(e.target.value)}
              required
              style={{ flex: '1 1 300px' }}
            />
            <button className="btn btn-detail" type="submit" disabled={isSubmittingQuiz}>
              {isSubmittingQuiz ? 'Mengirim...' : 'Kirim Jawaban'}
            </button>
          </form>
        ) : null}

        {hasilKuis ? (
          <p style={{ marginTop: '12px', color: hasilKuis.includes('Benar') ? 'var(--green)' : 'var(--red)' }}>
            {hasilKuis}
          </p>
        ) : null}

        <p style={{ marginTop: '16px', marginBottom: '6px', color: 'var(--subtext0)' }}>Achievement saya:</p>
        {unlockedAchievements.length === 0 ? (
          <p style={{ margin: 0 }}>Belum ada achievement yang terbuka.</p>
        ) : (
          <ul style={{ margin: 0, paddingLeft: '18px' }}>
            {unlockedAchievements.map((achievement) => (
              <li key={achievement.achievementId}>
                {achievement.name} - {achievement.description}
              </li>
            ))}
          </ul>
        )}
      </section>

            <section className="form-card" style={{maxWidth: 'none'}}>
                <h3 style={{color: 'var(--lavender)', marginTop: 0}}>Forum Diskusi ({relatedComments.length} Komentar
                    Utama)</h3>

                {/* Form Tambah Komentar Utama - Diubah menjadi in-line agar lebih modern */}
                <form onSubmit={handleMainCommentSubmit}
                      style={{marginBottom: '20px', display: 'flex', flexDirection: 'column', gap: '10px'}}>
                    <textarea
                        className="input-entry"
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                        placeholder="Tuliskan komentar Anda untuk bacaan ini..."
                        rows="3"
                        required
                        style={{width: '100%', resize: 'vertical'}}
                    />
                    <button
                        className="btn btn-add"
                        type="submit"
                        disabled={isSubmittingComment}
                        style={{alignSelf: 'flex-end'}}
                    >
                        {isSubmittingComment ? 'Mengirim...' : 'Kirim Komentar'}
                    </button>
                </form>

                <hr style={{borderColor: 'var(--surface1)', margin: '20px 0'}}/>

                {/* Daftar Komentar Rekursif */}
                {relatedComments.length === 0 ? (
                    <p style={{color: 'var(--subtext0)'}}>Belum ada komentar untuk bacaan ini. Jadilah yang pertama!</p>
                ) : (
                    <div style={{display: 'flex', flexDirection: 'column', gap: '16px'}}>
                        {relatedComments.map((comment) => (
                            <CommentItem
                                key={comment.id}
                                comment={comment}
                                bacaanId={id}
                                onCommentRefresh={fetchCommentsOnly}
                                    currentUser={currentUser}
                            />
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
}
