import React from "react";
import './Header.css';
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

const Header = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate("/login"); 
  };

  return (
    <header className="bg-primary text-white mb-3 shadow-sm">
      <nav className="navbar navbar-expand-lg navbar-dark container">
        <Link className="navbar-brand d-flex align-items-center" to="/">
          <img
            src="/logo.jpg"
            alt="Logo"
            width="30"
            height="30"
            className="me-2"
          />
          <strong>Family Tree</strong>
        </Link>

        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon" />
        </button>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
            {/* Dropdown Menu */}
            <li className="nav-item dropdown">
              <span
                className="nav-link dropdown-toggle"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                Family
              </span>
              <ul className="dropdown-menu">
                <li>
                  <Link className="dropdown-item" to="/family">My Family</Link>
                </li>
                <li>
                  <Link className="dropdown-item" to="/searchfamily">Browse Family</Link>
                </li>
              </ul>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/myProfile">My Profile</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/changepassword">Change Password</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" onClick={logout}>Logout</Link>
            </li>
          </ul>
        </div>
      </nav>
    </header>
  );
};

export default Header;
