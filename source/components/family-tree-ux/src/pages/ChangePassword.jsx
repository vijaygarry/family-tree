import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axiosInstance";

const ChangePassword = ({ changePassword }) => {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [touched, setTouched] = useState({});
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const validate = () => {
    return {
      currentPassword: currentPassword.trim() === "",
      newPassword: newPassword.trim() === "",
      confirmPassword: confirmPassword.trim() === "" || newPassword !== confirmPassword
    };
  };

  const errors = validate();
  const isValid = !Object.values(errors).some(Boolean);

  const handleSubmit = async (e) => {
    e?.preventDefault(); // Prevent page reload if used inside a form
    setTouched({ currentPassword: true, newPassword: true, confirmPassword: true });

    if (!isValid) return;

    try {
        await api.post("/session/changepassword", {
            currentPassword,
            newPassword,
        });
      setSuccess("Password changed successfully.");
      setError("");
      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");

      setTimeout(() => navigate("/home"), 1500);
    } catch (err) {
      console.error("Change password failed:", err);
      setError(
        err.response?.data?.error ||
        "Failed to change password. Please try again."
      );
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: "500px" }}>
      <h3 className="mb-4">Change Password</h3>

      {error && <div className="alert alert-danger">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <form onSubmit={handleSubmit}>
        {/* Current Password */}
        <div className="mb-3">
          <label className="form-label">
            Current Password <span className="text-danger">*</span>
          </label>
          <input
            type="password"
            className={`form-control ${touched.currentPassword && errors.currentPassword ? "is-invalid" : ""}`}
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            onBlur={() => setTouched((prev) => ({ ...prev, currentPassword: true }))}
          />
          {touched.currentPassword && errors.currentPassword && (
            <div className="invalid-feedback">Current password is required.</div>
          )}
        </div>

        {/* New Password */}
        <div className="mb-3">
          <label className="form-label">
            New Password <span className="text-danger">*</span>
          </label>
          <input
            type="password"
            className={`form-control ${touched.newPassword && errors.newPassword ? "is-invalid" : ""}`}
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            onBlur={() => setTouched((prev) => ({ ...prev, newPassword: true }))}
          />
          {touched.newPassword && errors.newPassword && (
            <div className="invalid-feedback">New password is required.</div>
          )}
        </div>

        {/* Confirm Password */}
        <div className="mb-3">
          <label className="form-label">
            Confirm New Password <span className="text-danger">*</span>
          </label>
          <input
            type="password"
            className={`form-control ${touched.confirmPassword && errors.confirmPassword ? "is-invalid" : ""}`}
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            onBlur={() => setTouched((prev) => ({ ...prev, confirmPassword: true }))}
          />
          {touched.confirmPassword && errors.confirmPassword && (
            <div className="invalid-feedback">
              {confirmPassword.trim() === ""
                ? "Please confirm your new password."
                : "Passwords do not match."}
            </div>
          )}
        </div>

        <button type="submit" className="btn btn-primary w-100">Change Password</button>
      </form>
    </div>
  );
};

export default ChangePassword;
