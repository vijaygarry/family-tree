import React from "react";
import { AuthProvider } from "./context/AuthContext";
import { Router, Routes, Route } from "react-router-dom";
import PrivateRoute from "./components/PrivateRoute";
import MainLayout from "./layout/MainLayout";
//import Home from "./pages/Home";
import Login from "./pages/Login";
//import ChangePassword from "./pages/ChangePassword";
import FamilyTreeApp from "./pages/FamilyTreeApp";
import FamilySearch from "./pages/FamilySearch";

//import UserDetails from "./pages/UserDetails";

const App = () => (

        <Routes>
          {/* Public route */}
          <Route path="/login" element={<Login />} />
          {/* Protected routes with layout */}
          <Route
          path="/family/:familyId"
          element={
            <PrivateRoute>
              <MainLayout>
                <FamilyTreeApp />
              </MainLayout>
            </PrivateRoute>
          }
        />
        <Route
          path="/searchfamily"
          element={
            <PrivateRoute>
              <MainLayout>
                <FamilySearch />
              </MainLayout>
            </PrivateRoute>
          }
        />
        </Routes>
);

export default App;
