import React from "react";
import { AuthProvider } from "./context/AuthContext";
import { Router, Routes, Route } from "react-router-dom";
import PrivateRoute from "./components/PrivateRoute";
import MainLayout from "./layout/MainLayout";
import LoginLayout from "./layout/LoginLayout";
import HomePage from "./pages/HomePage";
import Login from "./pages/Login";
//import ChangePassword from "./pages/ChangePassword";
import FamilyTreeApp from "./pages/FamilyTreeApp";
import FamilySearch from "./pages/FamilySearch";
import ChangePassword from './pages/ChangePassword';

//import UserDetails from "./pages/UserDetails";

const App = () => (

        <Routes>
          {/* Public route */}
          <Route 
          path="/login" 
          element={
            <LoginLayout>
              <Login />
            </LoginLayout>
          }
          />
          {/* Protected routes with layout */}
          <Route
          path="/"
          element={
            <PrivateRoute>
              <MainLayout>
                <HomePage />
              </MainLayout>
            </PrivateRoute>
          }
        />
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
        <Route path="/changepassword" 
          element={
            <PrivateRoute>
              <MainLayout>
                <ChangePassword />
              </MainLayout>
            </PrivateRoute>
        } />
        </Routes>
);

export default App;
