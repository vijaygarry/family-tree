import React, { useContext, useState } from "react";
import { AuthContext } from "../context/AuthContext";
import { useLocation, useNavigate } from "react-router-dom";

const Login = () => {
  const { login } = useContext(AuthContext);
  const [loginName, setLoginName] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || "/";

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    if (!loginName.trim()) {
      setError("Login name is required.");
      return;
    }
    if (!password.trim()) {
      setError("Password is required.");
      return;
    }
    
    try {
      const success = await login({ loginName, password });
      if (success) {
        navigate(from, { replace: true });  // Redirect back to original path
      } else {
        setError("Invalid credentials");
      }
    } catch (err) {
      console.error("Login failed:", err);
      if (!err.response) {
        setError("Server is not responding. Please try again later.");
      } else if (err.response.status === 401) {
        setError("Login failed. Please check your credentials.");
      } else {
        setError(err.response.data?.error || "Unexpected error occurred. Try again.");
      }
    } finally {
      setPassword(""); // Clear password after attempt
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: "400px" }}>
      <h3 className="mb-3">Login</h3>
      {error && <div className="alert alert-danger">{error}</div>}
      <form onSubmit={handleLogin}>
        <div className="mb-3">
          <label htmlFor="loginName" className="form-label">
            Login Name <span style={{ color: 'red' }}>*</span>
          </label>
          <input type="text" className="form-control" onChange={(e) => setLoginName(e.target.value)} />
        </div>
        <div className="mb-3">
          <label htmlFor="password" className="form-label">
            Password <span style={{ color: 'red' }}>*</span>
          </label>
          <input type="password" className="form-control" value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        <div className="form-text text-danger">* This field is required</div>
        <button className="btn btn-primary w-100">Login</button>
      </form>
    </div>
  );
};

export default Login;
