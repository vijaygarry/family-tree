import React, { useState } from "react";
import api from "../api/axiosInstance";

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [showRequestOTPForm, setShowRequestOTPForm] = useState(true);
  const [otp, setOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [resetError, setResetError] = useState("");
  const [resetPasswordSuccess, setResetPasswordSuccess] = useState("");
  const [requestId, setRequestId] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    if (!email) {
      setError("Email Id is required");
      return;
    }
    try {
      const response = await api.post("/session/requestForgotPasswordOTP", {
        emailId: email,
      });
      if (response && response.data && response.data.requestId) {
        setRequestId(response.data.requestId);
      }
      setShowRequestOTPForm(false);
    } catch (err) {
      setError("Failed to request reset link.");
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setResetError("");
    setResetPasswordSuccess("");
    if (!otp || !newPassword || !confirmPassword) {
      setResetError("All fields are required.");
      return;
    }
    if (newPassword !== confirmPassword) {
      setResetError("Passwords do not match.");
      return;
    }
    try {
      await api.post("/session/resetForgotPassword", {
        emailId: email,
        otp,
        newPassword,
        confirmPassword,
        requestId,
      });

      // Success: clear fields and show login link
      setOtp("");
      setNewPassword("");
      setConfirmPassword("");
      setShowRequestOTPForm(false);
      setResetPasswordSuccess(
        "Password reset successfully, please login using new password.",
      );
    } catch (err) {
      if (
        err.response &&
        err.response.data &&
        err.response.data.operationMessage
      ) {
        setResetError(err.response.data.operationMessage);
      } else {
        setResetError("Failed to reset password.");
      }
    }
  };

  // Form for requesting reset link
  const requestForgotPwdOTPForm = (
    <form onSubmit={handleSubmit}>
      <div style={{ marginBottom: 16 }}>
        <label htmlFor="email">Email Id</label>
        <input
          type="email"
          id="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          style={{
            width: "100%",
            padding: 8,
            marginTop: 4,
            borderRadius: 4,
            border: "1px solid #ccc",
          }}
          required
        />
      </div>
      {error && <div style={{ color: "red", marginBottom: 8 }}>{error}</div>}
      <button
        type="submit"
        style={{
          width: "100%",
          padding: 10,
          background: "#1976d2",
          color: "#fff",
          border: "none",
          borderRadius: 4,
          fontWeight: "bold",
        }}
      >
        Send One Time Password (OTP)
      </button>
    </form>
  );

  // Form for resetting password
  const forgotPasswordForm = (
    <form onSubmit={handleResetPassword}>
      <div style={{ marginBottom: 16 }}>
        <label>Email Id</label>
        <input
          type="email"
          value={email}
          readOnly
          style={{
            width: "100%",
            padding: 8,
            marginTop: 4,
            borderRadius: 4,
            border: "1px solid #ccc",
            background: "#f5f5f5",
          }}
        />
      </div>
      <div style={{ marginBottom: 16 }}>
        <label>One Time Password (OTP)</label>
        <input
          type="text"
          value={otp}
          onChange={(e) => setOtp(e.target.value)}
          style={{
            width: "100%",
            padding: 8,
            marginTop: 4,
            borderRadius: 4,
            border: "1px solid #ccc",
          }}
          required
        />
      </div>
      <div style={{ marginBottom: 16 }}>
        <label>New Password</label>
        <input
          type="password"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          style={{
            width: "100%",
            padding: 8,
            marginTop: 4,
            borderRadius: 4,
            border: "1px solid #ccc",
          }}
          required
        />
      </div>
      <div style={{ marginBottom: 16 }}>
        <label>Confirm Password</label>
        <input
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          style={{
            width: "100%",
            padding: 8,
            marginTop: 4,
            borderRadius: 4,
            border: "1px solid #ccc",
          }}
          required
        />
      </div>
      {resetError && (
        <div style={{ color: "red", marginBottom: 8 }}>{resetError}</div>
      )}
      <button
        type="submit"
        style={{
          width: "100%",
          padding: 10,
          background: "#1976d2",
          color: "#fff",
          border: "none",
          borderRadius: 4,
          fontWeight: "bold",
        }}
      >
        Reset Password
      </button>
    </form>
  );

  // Success message
  const forgotPasswordSuccess = (
    <div style={{ color: "green", marginBottom: 8, textAlign: "center" }}>
      Password reset successfully, please login using new password.
      <br />
      <div style={{ display: "flex", justifyContent: "center", marginTop: 12 }}>
        <button
          onClick={() => (window.location.href = "/login")}
          style={{
            padding: "8px 16px",
            background: "#1976d2",
            color: "#fff",
            border: "none",
            borderRadius: 4,
            fontWeight: "bold",
            cursor: "pointer",
          }}
        >
          Login
        </button>
      </div>
    </div>
  );

  return (
    <div
      className="forgot-password-container"
      style={{
        maxWidth: 400,
        margin: "40px auto",
        padding: 24,
        border: "1px solid #eee",
        borderRadius: 8,
        background: "#fff",
      }}
    >
      <h2>Forgot Password</h2>
      {resetPasswordSuccess
        ? forgotPasswordSuccess
        : showRequestOTPForm
          ? requestForgotPwdOTPForm
          : forgotPasswordForm}
    </div>
  );
};

export default ForgotPassword;
