import React, { useEffect, useState } from "react";
import { useAuth } from "../auth/AuthContext";
import { userApi } from "../services/userApi";
import { useSnowContext } from "../context/SnowContext";

export function ProfilePage() {
  const { userInfo } = useAuth();
  const { enabled: snowEnabled, toggle: toggleSnow } = useSnowContext();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // получаем профиль текущего пользователя из JWT токена
    const load = async () => {
      if (!userInfo) {
        setLoading(false);
        return;
      }
      try {
        const data = await userApi.getCurrentUser();
        setProfile(data);
      } catch {
        setError("Не удалось загрузить профиль пользователя");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [userInfo]);

  if (loading) {
    return <div className="page">Загрузка профиля...</div>;
  }

  if (error) {
    return (
      <div className="page">
        <div className="error-text">{error}</div>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="page">
        <div>Профиль не найден</div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="card">
        <h1>Профиль</h1>
        <div className="profile-row">
          <span>Логин:</span>
          <strong>{profile.username}</strong>
        </div>
        <div className="profile-row">
          <span>Email:</span>
          <strong>{profile.email}</strong>
        </div>
        {profile.firstName && (
          <div className="profile-row">
            <span>Имя:</span>
            <strong>{profile.firstName}</strong>
          </div>
        )}
        {profile.lastName && (
          <div className="profile-row">
            <span>Фамилия:</span>
            <strong>{profile.lastName}</strong>
          </div>
        )}
        {profile.dateOfBirth && (
          <div className="profile-row">
            <span>Дата рождения:</span>
            <strong>{profile.dateOfBirth}</strong>
          </div>
        )}
        {profile.createdAt && (
          <div className="profile-row">
            <span>Создан:</span>
            <strong>{profile.createdAt}</strong>
          </div>
        )}
        <div className="profile-row" style={{ marginTop: "1.5rem", paddingTop: "1.5rem", borderTop: "1px solid rgba(148, 163, 184, 0.2)" }}>
          <span>Снежная тема:</span>
          <button
            className={snowEnabled ? "btn primary" : "btn secondary"}
            onClick={toggleSnow}
            style={{
              padding: "0.5rem 1rem",
              fontSize: "0.875rem",
            }}
          >
            {snowEnabled ? "Выключить" : "Включить"}
          </button>
        </div>
      </div>
    </div>
  );
}



