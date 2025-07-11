import React from "react";
import HeaderWithoutMenu from "./HeaderWithoutMenu";
import Footer from "./Footer";

const LoginLayout = ({ children }) => (
  <div className="d-flex flex-column min-vh-100">
    <HeaderWithoutMenu />
    <main className="flex-grow-1 container py-4">{children}</main>
    <Footer />
  </div>
);

export default LoginLayout;
