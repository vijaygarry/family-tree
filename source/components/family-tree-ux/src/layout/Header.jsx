import React from "react";
import "./Header.css";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

const Header = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const { user } = useAuth();

  const handleLogout = async () => {
    await logout();
    navigate("/login");
  };
  
  const fallbackAvatar = "/default-avatar.png"; // keep this also in public folder

  return (
    <header className="bg-white text-black mb-3 shadow-sm">
      <nav className="navbar navbar-expand-lg navbar-dark container">
        <Link className="navbar-brand d-flex align-items-center" to="/">
          <img src="/logo.svg" alt="Logo"/>         
        </Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
        >
          <span className="navbar-toggler-icon" />
        </button>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
 
          <li className="nav-item">
            <Link className="nav-link" to="/">
              Home
            </Link>
          </li>
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
                  <Link className="dropdown-item" to="/family">
                    My Family
                  </Link>
                </li>
                <li>
                  <Link className="dropdown-item" to="/searchfamily">
                    Browse Family
                  </Link>
                </li>
                <li>
                  <Link className="dropdown-item" to="/searchfamily">
                    Add Family
                  </Link>
                </li>
              </ul>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/events">
                Events
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/accounts">
                Accounts
              </Link>
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
                <span className="me-2">{user.firstName} {user.lastName}</span>
                <img
  src={user.profileImageThumbnail}
  alt="User Avatar"
  className="rounded-circle me-2"
  width="25"
  height="25"
  onError={(e) => (e.currentTarget.src = fallbackAvatar)}
/>
              </a>
              <ul
                className="dropdown-menu dropdown-menu-end"
                aria-labelledby="userDropdown"
              >
                <li>
                  <Link className="dropdown-item" to="/myProfile">
                    My Profile
                  </Link>
                </li>
                <li>
                  <Link className="dropdown-item" to="/changepassword">
                    Change Password
                  </Link>
                </li>
                <li>
                  <hr className="dropdown-divider" />
                </li>
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
