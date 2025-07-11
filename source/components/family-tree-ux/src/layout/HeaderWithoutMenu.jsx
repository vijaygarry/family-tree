import React from "react";
import { Link } from "react-router-dom";

const HeaderWithoutMenu = () => {
  return (
    <header className="bg-primary text-white mb-3 shadow-sm">
      <nav className="navbar navbar-expand-lg navbar-dark container">
        <Link className="navbar-brand" to="/">Family Tree</Link>

        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon" />
        </button>
      </nav>
    </header>
  );
};

export default HeaderWithoutMenu;
