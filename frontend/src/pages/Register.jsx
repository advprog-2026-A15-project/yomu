import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../services/authService';

export default function Register() {
  const [formData, setFormData] = useState({
    username: '',
    displayName: '',
    email: '',
    phoneNumber: '',
    password: '',
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        username: formData.username,
        displayName: formData.displayName,
        email: formData.email || null,
        phoneNumber: formData.phoneNumber || null,
        password: formData.password,
      };
      await register(payload);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || 'Registrasi gagal');
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <h2 className="auth-title">Daftar Akun Yomu</h2>
        <p className="auth-subtitle">
          Atau{' '}
          <Link to="/login" className="auth-link">
            masuk jika sudah punya akun
          </Link>
        </p>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div>
            <label htmlFor="username" className="auth-label">Username *</label>
            <input
              id="username"
              name="username"
              type="text"
              required
              value={formData.username}
              onChange={handleChange}
              className="auth-input"
            />
          </div>

          <div>
            <label htmlFor="displayName" className="auth-label">Nama Tampilan *</label>
            <input
              id="displayName"
              name="displayName"
              type="text"
              required
              value={formData.displayName}
              onChange={handleChange}
              className="auth-input"
            />
          </div>

          <div>
            <label htmlFor="email" className="auth-label">Email (opsional)</label>
            <input
              id="email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              className="auth-input"
            />
          </div>

          <div>
            <label htmlFor="phoneNumber" className="auth-label">Nomor HP (opsional)</label>
            <input
              id="phoneNumber"
              name="phoneNumber"
              type="tel"
              value={formData.phoneNumber}
              onChange={handleChange}
              className="auth-input"
            />
          </div>

          <div>
            <label htmlFor="password" className="auth-label">Kata Sandi *</label>
            <input
              id="password"
              name="password"
              type="password"
              required
              value={formData.password}
              onChange={handleChange}
              className="auth-input"
            />
          </div>

          {error && <div className="auth-error">{error}</div>}

          <button type="submit" className="btn btn-add">
            Daftar
          </button>
        </form>
      </div>
    </div>
  );
}