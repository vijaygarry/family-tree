import React, { useState } from 'react';
import api from "../api/axiosInstance";

const SignUp = () => {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [showRequestOtpForm, setShowRequestOtpForm] = useState(true);
  const [otp, setOtp] = useState('');
  const [logonName, setLogonName] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [resetError, setResetError] = useState('');
  const [signUpSuccess, setSignUpSuccess] = useState('');
  const [requestId, setRequestId] = useState('');

  const requestOtp = async (e) => {
    e.preventDefault();
    setError('');
    if (!email) {
      setError('Email Id is required');
      return;
    }
    try {
      const response = await api.post("/session/requestSignupOtp", { emailId : email });
      setRequestId(response?.data?.requestId || '');
      setLogonName(response?.data?.logonName || '');
      setShowRequestOtpForm(false);
    } catch (err) {
      setError('Failed to request signup OTP.');
    }
  };
  

  const handleSignUp = async (e) => {
    e.preventDefault();
    setResetError('');
    setSignUpSuccess('');
    if (!otp || !password || !confirmPassword) {
      setResetError('All fields are required.');
      return;
    }
    if (password !== confirmPassword) {
      setResetError('Passwords do not match.');
      return;
    }
    try {
      await api.post("/session/signUp", { emailId: email, logonName: logonName, otp, password, requestId });

      // Success: clear fields and show login link
      setOtp('');
      setPassword('');
      setLogonName('');
      setConfirmPassword('');
      setSignUpSuccess('Password reset successfully, please login using new password.');
    } catch (err) {
      if (err.response && err.response.data && err.response.data.operationMessage) {
        setResetError(err.response.data.operationMessage);
      } else {
        setResetError('Failed to reset password.');
      }
    }
  };

  return (
    <div className="signup-container" style={{ maxWidth: 400, margin: '40px auto', padding: 24, border: '1px solid #eee', borderRadius: 8, background: '#fff' }}>
      <h2>Sign Up</h2>
      {signUpSuccess ? (
        <div style={{ color: 'green', marginBottom: 8, textAlign: 'center' }}>
          Sign Up completed successfully, please login using below link.<br />
          <div style={{ display: 'flex', justifyContent: 'center', marginTop: 12 }}>
            <button onClick={() => window.location.href = '/login'} style={{ padding: '8px 16px', background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, fontWeight: 'bold', cursor: 'pointer' }}>
              Login
            </button>
          </div>
        </div>
      ) : showRequestOtpForm ? (
        <form onSubmit={requestOtp}>
          <div style={{ marginBottom: 16 }}>
            <label htmlFor="email">Email Id</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{ width: '100%', padding: 8, marginTop: 4, borderRadius: 4, border: '1px solid #ccc' }}
              required
            />
          </div>
          {error && <div style={{ color: 'red', marginBottom: 8 }}>{error}</div>}
          <button type="submit" style={{ width: '100%', padding: 10, background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, fontWeight: 'bold' }}>
            Request Sign Up OTP
          </button>
        </form>
      ) : (
        <form onSubmit={handleSignUp}>
          <div style={{ marginBottom: 16 }}>
            <label>Email Id</label>
            <input
              type="email"
              value={email}
              readOnly
              style={{ width: '100%', padding: 8, marginTop: 4, borderRadius: 4, border: '1px solid #ccc', background: '#f5f5f5' }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label>One Time Password (OTP)</label>
            <input
              type="text"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              style={{ width: '100%', padding: 8, marginTop: 4, borderRadius: 4, border: '1px solid #ccc' }}
              required
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label>Logon Name</label>
            <input
              type="text"
              value={logonName}
              onChange={(e) => setLogonName(e.target.value)}
              style={{ width: '100%', padding: 8, marginTop: 4, borderRadius: 4, border: '1px solid #ccc' }}
              required
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={{ width: '100%', padding: 8, marginTop: 4, borderRadius: 4, border: '1px solid #ccc' }}
              required
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label>Confirm Password</label>
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              style={{ width: '100%', padding: 8, marginTop: 4, borderRadius: 4, border: '1px solid #ccc' }}
              required
            />
          </div>
          {resetError && <div style={{ color: 'red', marginBottom: 8 }}>{resetError}</div>}
          <button type="submit" style={{ width: '100%', padding: 10, background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, fontWeight: 'bold' }}>
            Sign Up
          </button>
        </form>
      )}
    </div>
  );
};

export default SignUp;
