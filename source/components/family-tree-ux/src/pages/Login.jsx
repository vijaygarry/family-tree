import { useContext, useState } from "react";
import { AuthContext } from "../context/AuthContext";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";

const Login = () => {
  const { login } = useContext(AuthContext);
  const [loginName, setLoginName] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

  const [searchParams] = useSearchParams();
  const redirectTo = searchParams.get("redirectTo");

  const from = redirectTo || location.state?.from?.pathname || "/";

  const handleLogin = async (e) => {
    e.preventDefault();
    // setLoading(true);
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
        navigate(from, { replace: true }); // Redirect back to original path
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
        setError(
          err.response.data?.error || "Unexpected error occurred. Try again.",
        );
      }
    } finally {
      setPassword(""); // Clear password after attempt
    }
  };

  return (
    <div className="container my-5 p-4 bg-white rounded" style={{ maxWidth: "500px" }}>
    <div className="text-center mb-4">
      <img src="/log-in-img.svg" alt="Logo" className="mb-3" />
      <h3 className="mb-3 fw-bold">Sign in to your Account</h3>
      </div>
      {error && <div className="alert alert-danger">{error}</div>}
      <form onSubmit={handleLogin}>
        <div className="mb-3">
          <label htmlFor="loginName" className="form-label fw-semibold">
            Login Name <span style={{ color: "red" }}>*</span>
          </label>
          <input
            type="text"
            className="form-control" style={{ minHeight: "45px" }}
            onChange={(e) => setLoginName(e.target.value)}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="password" className="form-label fw-semibold">
            Password <span style={{ color: "red" }}>*</span>
          </label>
          <input
            type="password"
            className="form-control" style={{ minHeight: "45px" }}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <div className="form-text text-danger">* This field is required</div>
        <button className="btn btn-primary w-100 fw-bold" style={{ background: "#A42502", borderColor:"#A42502" }}>Login</button>
        <div className="text-end my-2 fw-semibold">
          <a href="/forgotpassword" style={{ color:"#A42502",textDecoration:"none" }}>Forgot Password?</a>
        </div>
        <button
          onClick={() => (window.location.href = "/signup")}
          className="btn btn-secondary w-100 mt-3 fw-bold" style={{ background: "transparent", borderColor:"#A42502" ,color:"#A42502" }}
        >
          Sign-up
        </button>
      </form>
    </div>
  );
};

export default Login;
