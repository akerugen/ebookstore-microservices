import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import { CatalogPage } from "./pages/CatalogPage";
import { BookDetailsPage } from "./pages/BookDetailsPage";
import { AuthorDetailsPage } from "./pages/AuthorDetailsPage";
import { ProfilePage } from "./pages/ProfilePage";
import { SuperUserPanelPage } from "./pages/SuperUserPanelPage";
import { Layout } from "./components/Layout";

export function App() {
  return (
    <AuthProvider>
      <Layout>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/catalog" element={<CatalogPage />} />
          <Route path="/books/:id" element={<BookDetailsPage />} />
          <Route path="/authors/:id" element={<AuthorDetailsPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/admin/users" element={<SuperUserPanelPage />} />
          <Route path="/" element={<Navigate to="/catalog" replace />} />
          <Route path="*" element={<Navigate to="/catalog" replace />} />
        </Routes>
      </Layout>
    </AuthProvider>
  );
}



