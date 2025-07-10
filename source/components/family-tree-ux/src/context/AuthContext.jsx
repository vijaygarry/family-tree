import React, { createContext, useState, useContext, useEffect } from "react";
import api from "../api/axiosInstance"; // your axios instance

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null); // you can store user info from backend
  const [loading, setLoading] = useState(true);

  // On app load, verify session
  useEffect(() => {
    api.get("/session/getsessiondetail", { withCredentials: true }) // change to your backend "who am I" endpoint
      .then((res) => {
        setUser(res.data);
      })
      .catch(() => {
        setUser(null);
      })
      .finally(() => setLoading(false));
  }, []);

  // Login function
  const login = async (credentials) => {
    // credentials = { username, password }
    try {
      await api.post("/session/login", credentials, { withCredentials: true });
      // After successful login, fetch user info
      const res = await api.get("/session/getsessiondetail");
      setUser(res.data);
      return true;
    } catch (err) {
      throw err;
    }
  };

  // Logout function
  const logout = async () => {
    try {
      await api.post("/session/logout");
    } catch (err) {
      console.error("Logout error", err);
    } finally {
      setUser(null);
    }
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
