import React from "react";
import { useAuth } from "../context/AuthContext";

const HomePage = () => {
  const { user } = useAuth();

  return (
    <div className="container mt-4">
      <h2>Welcome, {user?.firstName || "Family Member"}!</h2>
    </div>
  );
};

export default HomePage;
