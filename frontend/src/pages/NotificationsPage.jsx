import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { notificationApi } from "../services/notificationApi";
import { useAuth } from "../auth/AuthContext";
import { hasRole } from "../utils/jwt";

export function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { userInfo } = useAuth();
  const navigate = useNavigate();

  // проверяем, является ли пользователь администратором
  const isAdmin = hasRole(userInfo, ["ROLE_ADMIN", "ROLE_SUPER_USER"]);

  useEffect(() => {
    const loadNotifications = async () => {
      try {
        setLoading(true);
        const data = await notificationApi.getNotifications();
        // фильтруем уведомления: для администраторов показываем все как прочитанные
        const filteredData = Array.isArray(data) ? data : [];
        setNotifications(filteredData);
      } catch (e) {
        console.error("Failed to load notifications:", e);
        setError("Не удалось загрузить уведомления");
        setNotifications([]);
      } finally {
        setLoading(false);
      }
    };

    loadNotifications();
  }, []);

  const handleNotificationClick = async (notification) => {
    try {
      // отмечаем уведомление как прочитанное, если оно еще не прочитано
      if (!notification.isRead && !isAdmin) {
        await notificationApi.markAsRead(notification.id);
        // обновляем состояние локально
        setNotifications((prev) =>
          prev.map((n) =>
            n.id === notification.id ? { ...n, isRead: true } : n
          )
        );
      }

      // если есть bookId, переходим на страницу книги
      if (notification.bookId) {
        navigate(`/books/${notification.bookId}`);
      }
    } catch (e) {
      console.error("Failed to mark notification as read:", e);
    }
  };

  const getNotificationTypeLabel = (type) => {
    const labels = {
      BOOK_CREATED: "Новая книга",
      BOOK_UPDATED: "Обновление книги",
      BOOK_DELETED: "Книга удалена",
      PRICE_CHANGED: "Изменение цены",
    };
    return labels[type] || type;
  };

  if (loading) {
    return (
      <div className="container">
        <h1>Новинки</h1>
        <p>Загрузка...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container">
        <h1>Новинки</h1>
        <p style={{ color: "#ef4444" }}>{error}</p>
      </div>
    );
  }

  return (
    <div className="container">
      <h1>Новинки</h1>
      {notifications.length === 0 ? (
        <p>Нет уведомлений</p>
      ) : (
        <div style={{ marginTop: "1.5rem" }}>
          {notifications.map((notification) => (
            <div
              key={notification.id}
              onClick={() => handleNotificationClick(notification)}
              style={{
                padding: "1rem",
                marginBottom: "1rem",
                borderRadius: "0.5rem",
                border: "1px solid rgba(148, 163, 184, 0.4)",
                background: notification.isRead || isAdmin
                  ? "rgba(15, 23, 42, 0.5)"
                  : "rgba(59, 130, 246, 0.1)",
                cursor: notification.bookId ? "pointer" : "default",
                transition: "all 0.2s",
              }}
              onMouseEnter={(e) => {
                if (notification.bookId) {
                  e.currentTarget.style.background = notification.isRead || isAdmin
                    ? "rgba(15, 23, 42, 0.7)"
                    : "rgba(59, 130, 246, 0.2)";
                }
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.background = notification.isRead || isAdmin
                  ? "rgba(15, 23, 42, 0.5)"
                  : "rgba(59, 130, 246, 0.1)";
              }}
            >
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "flex-start",
                  marginBottom: "0.5rem",
                }}
              >
                <h3 style={{ margin: 0, fontSize: "1.1rem" }}>
                  {notification.title}
                </h3>
                <span
                  style={{
                    padding: "0.25rem 0.5rem",
                    borderRadius: "0.25rem",
                    fontSize: "0.85rem",
                    background: "rgba(59, 130, 246, 0.2)",
                    color: "#bfdbfe",
                  }}
                >
                  {getNotificationTypeLabel(notification.type)}
                </span>
              </div>
              <p style={{ margin: "0.5rem 0", color: "#9ca3af" }}>
                {notification.message}
              </p>
              <div
                style={{
                  fontSize: "0.85rem",
                  color: "#6b7280",
                  marginTop: "0.5rem",
                }}
              >
                {new Date(notification.createdAt).toLocaleString("ru-RU")}
                {!notification.isRead && !isAdmin && (
                  <span
                    style={{
                      marginLeft: "1rem",
                      padding: "0.2rem 0.5rem",
                      borderRadius: "0.25rem",
                      background: "#3b82f6",
                      color: "white",
                      fontSize: "0.75rem",
                    }}
                  >
                    Новое
                  </span>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

