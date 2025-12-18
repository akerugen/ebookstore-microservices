import React, { useEffect, useState } from "react";
import { useAuth } from "../auth/AuthContext";
import { hasRole } from "../utils/jwt";
import { userApi } from "../services/userApi";

export function SuperUserPanelPage() {
  const { userInfo } = useAuth();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const isSuperUser = hasRole(userInfo, ["ROLE_SUPER_USER"]);

  useEffect(() => {
    const load = async () => {
      if (!isSuperUser) {
        setLoading(false);
        return;
      }
      try {
        const data = await userApi.getAllUsers();
        setUsers(data);
      } catch {
        setError("Не удалось загрузить пользователей");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [isSuperUser]);

  if (!isSuperUser) {
    return (
      <div className="page">
        <div className="error-text">
          доступ запрещён. панель доступна только для SUPERUSER.
        </div>
      </div>
    );
  }

  if (loading) {
    return <div className="page">Загрузка пользователей...</div>;
  }

  if (error) {
    return (
      <div className="page">
        <div className="error-text">{error}</div>
      </div>
    );
  }

  return (
    <div className="page">
      <h1>Управление пользователями</h1>
      <div className="card">
        <div className="muted">
          здесь позже можно будет менять роли пользователей через backend
          endpoint. сейчас это вьюха для проверки интеграции user-service.
        </div>
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Логин</th>
              <th>Email</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.username}</td>
                <td>{u.email}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}



