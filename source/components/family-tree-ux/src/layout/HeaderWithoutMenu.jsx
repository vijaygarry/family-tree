import React from "react";
import { Link, NavLink } from "react-router-dom";
const HeaderWithoutMenu = () => {
  return (
    <header className="bg-white text-black mb-3 shadow-sm">
      <nav className="navbar navbar-expand-lg navbar-dark container">
        <Link className="navbar-brand d-flex align-items-center" to="/">
          <img src="/logo.svg" alt="Logo" height={60} />
        </Link>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
        >
          <span className="navbar-toggler-icon" />
        </button>

        {/* Navbar Links */}
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
            <li className="nav-item">
              <NavLink to="/login" className={({ isActive }) => "nav-link" + (isActive ? " active" : "")}>
                Login
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink to="/helpWithoutMenu" className={({ isActive }) => "nav-link" + (isActive ? " active" : "")}>
                Help
              </NavLink>
            </li>
          </ul>
        </div>
      </nav>
    </header>
  );
};

export default HeaderWithoutMenu;
