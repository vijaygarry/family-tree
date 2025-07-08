import React from "react";
import { Link } from "react-router-dom";

const Header = () => {
  return (
    <header className="bg-primary text-white mb-3 shadow-sm">
      <nav className="navbar navbar-expand-lg navbar-dark container">
        <Link className="navbar-brand" to="/">Family Tree</Link>

        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon" />
        </button>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
            <li className="nav-item">
              <Link className="nav-link" to="/">Home</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/family">Family Details</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/user">User Details</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/change-password">Change Password</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/login">Logout</Link>
            </li>
          </ul>
        </div>
      </nav>
    </header>
  );
};

export default Header;
