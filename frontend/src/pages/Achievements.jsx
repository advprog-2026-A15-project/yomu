import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getToken, logout } from '../services/authService';

const API_BASE = 'http://localhost:8080';

const initialAchievementForm = {
  name: '',
  description: '',
  milestone: 1,
};

const initialDailyMissionForm = {
  name: '',
  description: '',
  milestone: 1,
  missionDate: '',
};

const formatDateTime = (value) => {
  if (!value) {
    return '-';
  }

  return new Date(value).toLocaleString('id-ID');
};

const formatDate = (value) => {
  if (!value) {
    return '-';
  }

  return new Date(value).toLocaleDateString('id-ID', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
};

const buildProgressWidth = (currentProgress, milestone) => {
  if (!milestone) {
    return '0%';
  }

  return `${Math.min((currentProgress / milestone) * 100, 100)}%`;
};

export default function Achievements() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [achievements, setAchievements] = useState([]);
  const [dailyMissions, setDailyMissions] = useState([]);
  const [adminAchievements, setAdminAchievements] = useState([]);
  const [adminDailyMissions, setAdminDailyMissions] = useState([]);
  const [achievementForm, setAchievementForm] = useState(initialAchievementForm);
  const [dailyMissionForm, setDailyMissionForm] = useState(initialDailyMissionForm);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [submitError, setSubmitError] = useState('');
  const [submitSuccess, setSubmitSuccess] = useState('');
  const [submittingKey, setSubmittingKey] = useState('');

  const loadDashboard = async (token, signal) => {
    const headers = {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    };

    const profileRes = await fetch(`${API_BASE}/api/auth/me`, { headers, signal });
    if (profileRes.status === 401 || profileRes.status === 403) {
      logout();
      navigate('/login');
      return;
    }

    if (!profileRes.ok) {
      const text = await profileRes.text();
      throw new Error(text || 'Gagal memuat profil user');
    }

    const profileData = await profileRes.json();
    setProfile(profileData);

    const [achievementsRes, dailyMissionsRes] = await Promise.all([
      fetch(`${API_BASE}/api/achievements/me`, { headers, signal }),
      fetch(`${API_BASE}/api/achievements/daily-missions/active`, { headers, signal }),
    ]);

    if (
      achievementsRes.status === 401 ||
      achievementsRes.status === 403 ||
      dailyMissionsRes.status === 401 ||
      dailyMissionsRes.status === 403
    ) {
      logout();
      navigate('/login');
      return;
    }

    if (!achievementsRes.ok) {
      const text = await achievementsRes.text();
      throw new Error(text || 'Gagal memuat achievement');
    }

    if (!dailyMissionsRes.ok) {
      const text = await dailyMissionsRes.text();
      throw new Error(text || 'Gagal memuat daily mission aktif');
    }

    const achievementData = await achievementsRes.json();
    const dailyMissionData = await dailyMissionsRes.json();
    setAchievements(Array.isArray(achievementData) ? achievementData : []);
    setDailyMissions(Array.isArray(dailyMissionData) ? dailyMissionData : []);

    if (profileData.role === 'ADMIN') {
      const [adminAchievementsRes, adminDailyMissionsRes] = await Promise.all([
        fetch(`${API_BASE}/api/admin/achievements`, { headers, signal }),
        fetch(`${API_BASE}/api/admin/daily-missions`, { headers, signal }),
      ]);

      if (
        adminAchievementsRes.status === 401 ||
        adminAchievementsRes.status === 403 ||
        adminDailyMissionsRes.status === 401 ||
        adminDailyMissionsRes.status === 403
      ) {
        logout();
        navigate('/login');
        return;
      }

      if (!adminAchievementsRes.ok) {
        const text = await adminAchievementsRes.text();
        throw new Error(text || 'Gagal memuat daftar achievement admin');
      }

      if (!adminDailyMissionsRes.ok) {
        const text = await adminDailyMissionsRes.text();
        throw new Error(text || 'Gagal memuat daftar daily mission admin');
      }

      const adminAchievementData = await adminAchievementsRes.json();
      const adminDailyMissionData = await adminDailyMissionsRes.json();
      setAdminAchievements(Array.isArray(adminAchievementData) ? adminAchievementData : []);
      setAdminDailyMissions(Array.isArray(adminDailyMissionData) ? adminDailyMissionData : []);
      return;
    }

    setAdminAchievements([]);
    setAdminDailyMissions([]);
  };

  useEffect(() => {
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    const controller = new AbortController();

    const run = async () => {
      setLoading(true);
      setError('');
      try {
        await loadDashboard(token, controller.signal);
      } catch (err) {
        if (err.name !== 'AbortError') {
          setError(err.message || 'Terjadi kesalahan saat memuat achievement');
        }
      } finally {
        setLoading(false);
      }
    };

    run();
    return () => controller.abort();
  }, [navigate]);

  const reloadDashboard = async () => {
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    await loadDashboard(token);
  };

  const handleAchievementFormChange = (event) => {
    const { name, value } = event.target;
    setAchievementForm((current) => ({
      ...current,
      [name]: name === 'milestone' ? Number(value) : value,
    }));
  };

  const handleDailyMissionFormChange = (event) => {
    const { name, value } = event.target;
    setDailyMissionForm((current) => ({
      ...current,
      [name]: name === 'milestone' ? Number(value) : value,
    }));
  };

  const handleCreateAchievement = async (event) => {
    event.preventDefault();

    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    setSubmittingKey('achievement');
    setSubmitError('');
    setSubmitSuccess('');

    try {
      const response = await fetch(`${API_BASE}/api/admin/achievements`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(achievementForm),
      });

      if (response.status === 401 || response.status === 403) {
        logout();
        navigate('/login');
        return;
      }

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || 'Gagal membuat achievement');
      }

      setAchievementForm(initialAchievementForm);
      setSubmitSuccess('Achievement baru berhasil dibuat.');
      await reloadDashboard();
    } catch (err) {
      setSubmitError(err.message || 'Terjadi kesalahan saat membuat achievement');
    } finally {
      setSubmittingKey('');
    }
  };

  const handleCreateDailyMission = async (event) => {
    event.preventDefault();

    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    setSubmittingKey('daily-mission');
    setSubmitError('');
    setSubmitSuccess('');

    try {
      const response = await fetch(`${API_BASE}/api/admin/daily-missions`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          ...dailyMissionForm,
          missionDate: dailyMissionForm.missionDate || null,
        }),
      });

      if (response.status === 401 || response.status === 403) {
        logout();
        navigate('/login');
        return;
      }

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || 'Gagal membuat daily mission');
      }

      setDailyMissionForm(initialDailyMissionForm);
      setSubmitSuccess('Daily mission baru berhasil dibuat.');
      await reloadDashboard();
    } catch (err) {
      setSubmitError(err.message || 'Terjadi kesalahan saat membuat daily mission');
    } finally {
      setSubmittingKey('');
    }
  };

  return (
    <div className="page-container" style={{ gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '16px', flexWrap: 'wrap' }}>
        <div>
          <h1 className="page-title" style={{ marginBottom: '8px' }}>
            {profile?.role === 'ADMIN' ? 'Achievement Module' : 'Achievement Saya'}
          </h1>
          <p className="status-note" style={{ margin: 0 }}>
            {profile ? `Login sebagai ${profile.displayName || profile.username} (${profile.role})` : 'Memuat profil...'}
          </p>
        </div>
        <Link to="/" style={{ color: 'var(--blue)', textDecoration: 'none' }}>Kembali ke Bacaan</Link>
      </div>

      {loading && <div className="status-note">Memuat achievement dan daily mission...</div>}
      {error && <div className="status-error">{error}</div>}

      {!loading && !error && (
        <>
          {profile?.role === 'ADMIN' ? (
            <section className="form-card" style={{ maxWidth: 'none' }}>
              <h2 style={{ marginTop: 0, color: 'var(--lavender)' }}>Panel Admin</h2>
              <p style={{ marginTop: 0, color: 'var(--subtext0)' }}>
                Buat achievement berdasarkan milestone bacaan, lalu buat daily mission aktif untuk tanggal tertentu.
              </p>

              {submitSuccess && <div className="status-note">{submitSuccess}</div>}
              {submitError && <div className="status-error">{submitError}</div>}

              <div style={{ display: 'grid', gap: '20px', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))' }}>
                <form onSubmit={handleCreateAchievement} style={{ display: 'grid', gap: '12px' }}>
                  <h3 style={{ margin: 0, color: 'var(--text)' }}>Buat Achievement</h3>
                  <input
                    className="input-entry"
                    name="name"
                    placeholder="Nama achievement"
                    value={achievementForm.name}
                    onChange={handleAchievementFormChange}
                    required
                  />
                  <textarea
                    className="input-entry"
                    name="description"
                    placeholder="Deskripsi singkat"
                    value={achievementForm.description}
                    onChange={handleAchievementFormChange}
                    rows={3}
                  />
                  <input
                    className="input-entry"
                    type="number"
                    min="1"
                    name="milestone"
                    placeholder="Milestone"
                    value={achievementForm.milestone}
                    onChange={handleAchievementFormChange}
                    required
                  />
                  <button className="btn btn-add" type="submit" disabled={submittingKey === 'achievement'}>
                    {submittingKey === 'achievement' ? 'Menyimpan...' : 'Simpan Achievement'}
                  </button>
                </form>

                <form onSubmit={handleCreateDailyMission} style={{ display: 'grid', gap: '12px' }}>
                  <h3 style={{ margin: 0, color: 'var(--text)' }}>Buat Daily Mission</h3>
                  <input
                    className="input-entry"
                    name="name"
                    placeholder="Nama mission"
                    value={dailyMissionForm.name}
                    onChange={handleDailyMissionFormChange}
                    required
                  />
                  <textarea
                    className="input-entry"
                    name="description"
                    placeholder="Deskripsi singkat"
                    value={dailyMissionForm.description}
                    onChange={handleDailyMissionFormChange}
                    rows={3}
                  />
                  <input
                    className="input-entry"
                    type="number"
                    min="1"
                    name="milestone"
                    placeholder="Milestone"
                    value={dailyMissionForm.milestone}
                    onChange={handleDailyMissionFormChange}
                    required
                  />
                  <input
                    className="input-entry"
                    type="date"
                    name="missionDate"
                    value={dailyMissionForm.missionDate}
                    onChange={handleDailyMissionFormChange}
                  />
                  <button className="btn btn-detail" type="submit" disabled={submittingKey === 'daily-mission'}>
                    {submittingKey === 'daily-mission' ? 'Menyimpan...' : 'Simpan Daily Mission'}
                  </button>
                </form>
              </div>

              <div style={{ display: 'grid', gap: '20px', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', marginTop: '24px' }}>
                <article className="thread-item">
                  <div className="thread-content">
                    <h3 className="thread-title">Daftar Achievement</h3>
                    {adminAchievements.length === 0 ? (
                      <p className="thread-excerpt">Belum ada achievement yang dibuat.</p>
                    ) : (
                      adminAchievements.map((achievement) => (
                        <div key={achievement.id} style={{ marginTop: '12px' }}>
                          <strong>{achievement.name}</strong>
                          <p className="thread-excerpt" style={{ marginBottom: '4px' }}>
                            {achievement.description || 'Tanpa deskripsi'}
                          </p>
                          <div className="thread-meta">Milestone: {achievement.milestone} bacaan selesai</div>
                        </div>
                      ))
                    )}
                  </div>
                </article>

                <article className="thread-item">
                  <div className="thread-content">
                    <h3 className="thread-title">Daftar Daily Mission</h3>
                    {adminDailyMissions.length === 0 ? (
                      <p className="thread-excerpt">Belum ada daily mission yang dibuat.</p>
                    ) : (
                      adminDailyMissions.map((mission) => (
                        <div key={mission.id} style={{ marginTop: '12px' }}>
                          <strong>{mission.name}</strong>
                          <p className="thread-excerpt" style={{ marginBottom: '4px' }}>
                            {mission.description || 'Tanpa deskripsi'}
                          </p>
                          <div className="thread-meta">
                            Target {mission.milestone} bacaan pada {formatDate(mission.missionDate)}
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </article>
              </div>
            </section>
          ) : null}

          <section className="thread-list" aria-label="Progress Achievement">
            <h2 style={{ marginTop: 0, color: 'var(--lavender)' }}>Progress Achievement</h2>
            {achievements.length === 0 ? (
              <div className="status-note">Belum ada achievement. Admin perlu menambahkan achievement baru.</div>
            ) : (
              achievements.map((achievement) => (
                <article className="thread-item" key={achievement.achievementId}>
                  <div className="thread-content">
                    <h3 className="thread-title" style={{ marginBottom: '6px' }}>
                      {achievement.name}
                    </h3>
                    <p className="thread-excerpt">
                      {achievement.description || 'Tanpa deskripsi'}
                    </p>
                    <div
                      style={{
                        height: '10px',
                        borderRadius: '999px',
                        backgroundColor: 'var(--surface0)',
                        overflow: 'hidden',
                        marginBottom: '8px',
                      }}
                    >
                      <div
                        style={{
                          width: buildProgressWidth(achievement.currentProgress, achievement.milestone),
                          height: '100%',
                          backgroundColor: achievement.unlocked ? 'var(--green)' : 'var(--blue)',
                          transition: 'width 0.2s ease',
                        }}
                      />
                    </div>
                    <div className="thread-meta">
                      Progress: {achievement.currentProgress}/{achievement.milestone} bacaan selesai
                    </div>
                    <div className="thread-meta">
                      Status: {achievement.unlocked ? 'Tercapai' : 'Belum tercapai'}
                    </div>
                    <div className="thread-meta">
                      Dicapai: {achievement.unlocked ? formatDateTime(achievement.achievedAt) : '-'}
                    </div>
                  </div>
                </article>
              ))
            )}
          </section>

          <section className="thread-list" aria-label="Daily Mission Aktif">
            <h2 style={{ marginTop: 0, color: 'var(--lavender)' }}>Daily Mission Aktif</h2>
            {dailyMissions.length === 0 ? (
              <div className="status-note">Belum ada daily mission aktif untuk hari ini.</div>
            ) : (
              dailyMissions.map((mission) => (
                <article className="thread-item" key={mission.dailyMissionId}>
                  <div className="thread-content">
                    <h3 className="thread-title" style={{ marginBottom: '6px' }}>
                      {mission.name}
                    </h3>
                    <p className="thread-excerpt">
                      {mission.description || 'Tanpa deskripsi'}
                    </p>
                    <div
                      style={{
                        height: '10px',
                        borderRadius: '999px',
                        backgroundColor: 'var(--surface0)',
                        overflow: 'hidden',
                        marginBottom: '8px',
                      }}
                    >
                      <div
                        style={{
                          width: buildProgressWidth(mission.currentProgress, mission.milestone),
                          height: '100%',
                          backgroundColor: mission.completed ? 'var(--green)' : 'var(--peach)',
                          transition: 'width 0.2s ease',
                        }}
                      />
                    </div>
                    <div className="thread-meta">
                      Progress hari ini: {mission.currentProgress}/{mission.milestone} bacaan selesai
                    </div>
                    <div className="thread-meta">
                      Tanggal aktif: {formatDate(mission.missionDate)}
                    </div>
                    <div className="thread-meta">
                      Status: {mission.completed ? 'Selesai' : 'Masih berjalan'}
                    </div>
                  </div>
                </article>
              ))
            )}
          </section>
        </>
      )}
    </div>
  );
}
