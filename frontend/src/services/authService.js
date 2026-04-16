import api from '../api/axios';

// Simpan token
const setToken = (token) => {
    localStorage.setItem('token', token);
};

const setRole = (role) => {
    if (role) {
        localStorage.setItem('role', role);
    } else {
        localStorage.removeItem('role');
    }
};

// Ambil token
export const getToken = () => {
    return localStorage.getItem('token');
};

export const getRole = () => {
    return localStorage.getItem('role');
};

// Hapus token
export const removeToken = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
};

// Login
export const login = async (identifier, password) => {
    const response = await api.post('/auth/login', {identifier, password});
    const {token, role} = response.data;
    setToken(token);
    setRole(role);
    return response.data;
};

// Register
export const register = async (userData) => {
    const response = await api.post('/auth/register', userData);
    const {token, role} = response.data;
    setToken(token);
    setRole(role);
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
        setRole(response.data?.role);
        return response.data;
    } catch {
        removeToken();
        return null;
    }
};

export const loginWithGoogle = async (idToken) => {
    const response = await api.post('/auth/oauth2/google', {idToken});
    const {token, role} = response.data;
    setToken(token);
    setRole(role);
    return response.data;
};