import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Mail, Lock } from 'lucide-react';
import { login } from '../services/authService';
import GoogleLoginButton from '../components/GoogleLoginButton';

export default function Login() {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || '/';

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await login(identifier, password);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err.response?.data?.error || 'Login gagal');
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <h2 className="auth-title">Selamat Datang Kembali</h2>
        <p className="auth-subtitle">Silakan masuk ke akun Yomu Anda</p>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div>
            <label htmlFor="identifier" className="auth-label">
              Username / Email / No. HP
            </label>
            <div className="auth-input-wrap">
              <Mail size={18} className="auth-icon" />
              <input
                id="identifier"
                type="text"
                required
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                className="auth-input with-icon"
                placeholder="Masukkan username, email, atau nomor HP"
              />
            </div>
          </div>

          <div>
            <label htmlFor="password" className="auth-label">
              Kata Sandi
            </label>
            <div className="auth-input-wrap">
              <Lock size={18} className="auth-icon" />
              <input
                id="password"
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="auth-input with-icon"
                placeholder="Masukkan kata sandi"
              />
            </div>
          </div>

          {error && <div className="auth-error">{error}</div>}

          <button type="submit" className="btn btn-detail">
            Masuk
          </button>
        </form>

        <div className="auth-separator" />
        <GoogleLoginButton />

        <p className="auth-footer">
          Belum punya akun?{' '}
          <Link to="/register" className="auth-link">
            Daftar sekarang
          </Link>
        </p>
      </div>
    </div>
  );
}