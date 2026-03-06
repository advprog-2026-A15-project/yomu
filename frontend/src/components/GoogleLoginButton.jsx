import { GoogleLogin } from '@react-oauth/google';
import { useNavigate } from 'react-router-dom';
import { loginWithGoogle } from '../services/authService';

export default function GoogleLoginButton() {
  const navigate = useNavigate();
  const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

  if (!googleClientId) {
    return null;
  }

  const handleSuccess = async (credentialResponse) => {
    try {
      await loginWithGoogle(credentialResponse.credential);
      navigate('/');
    } catch (error) {
      console.error('Google login failed', error);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center' }}>
      <GoogleLogin
        onSuccess={handleSuccess}
        onError={() => console.error('Google login failed')}
        useOneTap
        theme="outline"
        size="large"
        shape="rectangular"
        text="continue_with"
        locale="id"
      />
    </div>
  );
}
