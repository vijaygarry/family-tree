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

  const userName = "Vijay G."; // Replace with actual user name from context or props
  const userAvatar = "/user-avatar-man.png"; // Replace with actual user avatar URL

  return (
    <header className="bg-primary text-white mb-3 shadow-sm">
      <nav className="navbar navbar-expand-lg navbar-dark container">
        <Link className="navbar-brand d-flex align-items-center" to="/">
          <img
            src="/logo.png"
            alt="Logo"
            width="60"
            height="30"
            className="me-2"
          />
          <strong>Rajput Chippa Samaj</strong>
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
              <Link className="nav-link" to="/events">Events</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/accounts">Accounts</Link>
            </li>
            {/* User dropdown */}
            <li className="nav-item dropdown">
              <a
                href="#!"
                className="nav-link dropdown-toggle d-flex align-items-center"
                id="userDropdown"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                <span>{userName}</span>
                <img
                  src={userAvatar}
                  alt="User Avatar"
                  className="rounded-circle me-2"
                  width="32"
                  height="32"
                />
              </a>
              <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                <li>
                  <Link className="dropdown-item" to="/myProfile">My Profile</Link>
                </li>
                <li>
                  <Link className="dropdown-item" to="/changepassword">Change Password</Link>
                </li>
                <li><hr className="dropdown-divider" /></li>
                <li>
                  <button className="dropdown-item" onClick={handleLogout}>
                    Logout
                  </button>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </nav>
    </header>
  );
};

export default Header;
