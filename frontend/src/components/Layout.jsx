import React, { useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { hasRole } from "../utils/jwt";
import { setHttpAccessToken } from "../services/httpClient";

export function Layout({ children }) {
  const { isAuthenticated, userInfo, logout, accessToken } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    setHttpAccessToken(accessToken || null);
  }, [accessToken]);

  const onLogout = async () => {
    await logout();
    navigate("/login");
  };

  const isAdmin =
    userInfo && hasRole(userInfo, ["ROLE_ADMIN", "ROLE_SUPER_USER"]);
  const isSuperUser = userInfo && hasRole(userInfo, ["ROLE_SUPER_USER"]);

  return (
    <div className="app-root">
      <header className="app-header">
        <div className="logo" onClick={() => navigate("/catalog")}>
          Ebookstore
        </div>
        <nav className="nav-links">
          <Link
            to="/catalog"
            className={location.pathname.startsWith("/catalog") ? "active" : ""}
          >
            Каталог
          </Link>
          {isSuperUser && (
            <Link
              to="/admin/users"
              className={
                location.pathname.startsWith("/admin/users") ? "active" : ""
              }
            >
              Пользователи
            </Link>
          )}
        </nav>
        <div className="auth-block">
          {isAuthenticated ? (
            <>
              <span
                className="user-label"
                onClick={() => navigate("/profile")}
                style={{ cursor: "pointer", textDecoration: "underline" }}
              >
                {userInfo?.sub} ({userInfo?.role})
              </span>
              <button className="btn secondary" onClick={onLogout}>
                Выйти
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn secondary">
                Войти
              </Link>
              <Link to="/register" className="btn primary">
                Регистрация
              </Link>
            </>
          )}
        </div>
      </header>
      <main className="app-main">{children}</main>
      <footer className="app-footer">created by akerugen, 2025</footer>
    </div>
  );
}



