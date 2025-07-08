import React from "react";

const Footer = () => {
  return (
    <footer className="bg-light border-top text-center text-muted py-3 mt-auto">
      <div className="container">
        <small>&copy; {new Date().getFullYear()} Family Tree App. All rights reserved.</small>
      </div>
    </footer>
  );
};

export default Footer;
