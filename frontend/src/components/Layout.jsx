import React, { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { hasRole } from "../utils/jwt";
import { setHttpAccessToken } from "../services/httpClient";
import { notificationApi } from "../services/notificationApi";
import { useSnowContext } from "../context/SnowContext";
import { SnowAnimation } from "./SnowAnimation";

export function Layout({ children }) {
  const { isAuthenticated, userInfo, logout, accessToken } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [unreadCount, setUnreadCount] = useState(0);
  const { enabled: snowEnabled } = useSnowContext();

  useEffect(() => {
    setHttpAccessToken(accessToken || null);
  }, [accessToken]);

  // загружаем количество непрочитанных уведомлений
  useEffect(() => {
    if (!isAuthenticated) {
      setUnreadCount(0);
      return;
    }

    const isAdmin = userInfo && hasRole(userInfo, ["ROLE_ADMIN", "ROLE_SUPER_USER"]);
    // для администраторов не показываем счетчик
    if (isAdmin) {
      setUnreadCount(0);
      return;
    }

    const loadUnreadCount = async () => {
      try {
        const count = await notificationApi.getUnreadCount();
        setUnreadCount(count);
      } catch (e) {
        console.error("Failed to load unread count:", e);
      }
    };

    loadUnreadCount();
    // обновляем счетчик каждые 30 секунд
    const interval = setInterval(loadUnreadCount, 30000);
    return () => clearInterval(interval);
  }, [isAuthenticated, userInfo, location.pathname]);

  const onLogout = async () => {
    await logout();
    navigate("/login");
  };

  const isAdmin =
    userInfo && hasRole(userInfo, ["ROLE_ADMIN", "ROLE_SUPER_USER"]);
  const isSuperUser = userInfo && hasRole(userInfo, ["ROLE_SUPER_USER"]);

  return (
    <div className="app-root">
      <SnowAnimation enabled={snowEnabled} />
      <header className="app-header">
        <div style={{ display: "flex", alignItems: "center", gap: "1.5rem" }}>
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
            <Link
              to="/notifications"
              className={location.pathname.startsWith("/notifications") ? "active" : ""}
              style={{ position: "relative" }}
            >
              Новинки
              {!isAdmin && unreadCount > 0 && (
                <span
                  style={{
                    position: "absolute",
                    top: "-8px",
                    right: "-8px",
                    background: "#ef4444",
                    color: "white",
                    borderRadius: "50%",
                    width: "20px",
                    height: "20px",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    fontSize: "0.75rem",
                    fontWeight: "bold",
                  }}
                >
                  {unreadCount > 99 ? "99+" : unreadCount}
                </span>
              )}
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
        </div>
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



