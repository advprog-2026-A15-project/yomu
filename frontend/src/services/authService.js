import api from '../api/axios';

// Simpan token
const setToken = (token) => {
  localStorage.setItem('token', token);
};

// Ambil token
export const getToken = () => {
  return localStorage.getItem('token');
};

// Hapus token
export const removeToken = () => {
  localStorage.removeItem('token');
};

// Login
export const login = async (identifier, password) => {
  const response = await api.post('/auth/login', { identifier, password });
  const { token } = response.data;
  setToken(token);
  return response.data;
};

// Register
export const register = async (userData) => {
  const response = await api.post('/auth/register', userData);
  const { token } = response.data;
  setToken(token);
  return response.data;
};

// Logout
export const logout = () => {
  removeToken();
};

// Mendapatkan data user saat ini (jika perlu)
export const getCurrentUser = async () => {
  const token = getToken();
  if (!token) return null;
  try {
    const response = await api.get('/auth/me');
    return response.data;
  } catch {
    removeToken();
    return null;
  }
};

export const loginWithGoogle = async (idToken) => {
  const response = await api.post('/auth/oauth2/google', { idToken });
  const { token } = response.data;
  setToken(token);
  return response.data;
};