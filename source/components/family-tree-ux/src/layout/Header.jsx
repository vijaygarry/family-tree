import React, { useEffect, useState } from "react";
import "./Header.css";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { NavLink } from "react-router-dom";

const Header = () => {
  const { logout, user } = useAuth();
  const navigate = useNavigate();
  const [scrolled, setScrolled] = useState(false);

  const handleLogout = async () => {
    await logout();
    navigate("/login");
  };

  const fallbackAvatar = "/default-avatar.png"; // keep this also in public folder

  // ✅ Add scroll listener
  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 50) {
        setScrolled(true);
      } else {
        setScrolled(false);
      }
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  return (
    <header
      className={`bg-white text-black shadow-sm sticky-sm-top header ${
        scrolled ? "header-scrolled" : ""
      }`}
    >
      <nav className="navbar navbar-expand-lg navbar-dark container">
        <Link className="navbar-brand d-flex align-items-center" to="/">
          <img
            src="/logo.svg"
            alt="Logo"
            className={`logo ${scrolled ? "logo-small" : ""}`}
          />
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
  <NavLink
    to="/"
    className={({ isActive }) =>
      "nav-link" + (isActive ? " active" : "")
    }
  >
    Home
  </NavLink>
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
  <NavLink
    to="/events"
    className={({ isActive }) =>
      "nav-link" + (isActive ? " active" : "")
    }
  >
    Events
  </NavLink>
</li>
<li className="nav-item">
  <NavLink
    to="/accounts"
    className={({ isActive }) =>
      "nav-link" + (isActive ? " active" : "")
    }
  >
    Accounts
  </NavLink>
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
                <span className="me-2">
                  {user.firstName} {user.lastName}
                </span>
                <img
                  src={user.profileImageThumbnail}
                  alt="User Avatar"
                  className={`rounded-circle me-2 user-avatar ${
                    scrolled ? "avatar-small" : ""
                  }`}
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
