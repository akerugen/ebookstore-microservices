import React, { useEffect, useState } from "react";
import { useAuth } from "../auth/AuthContext";
import { hasRole } from "../utils/jwt";
import { userApi } from "../services/userApi";
import { authApi } from "../services/authApi";
import { ToastContainer } from "../components/Toast";
import { useToast } from "../hooks/useToast";

export function SuperUserPanelPage() {
  const { userInfo } = useAuth();
  const [users, setUsers] = useState([]);
  const [userRoles, setUserRoles] = useState({}); // {username: role}
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [changingRole, setChangingRole] = useState(null);
  const { toasts, showToast, removeToast } = useToast();

  const isSuperUser = hasRole(userInfo, ["ROLE_SUPER_USER"]);

  const roles = [
    { value: "USER", label: "Пользователь" },
    { value: "ADMIN", label: "Администратор" },
    { value: "SUPER_USER", label: "Суперпользователь" }
  ];

  const handleRoleChange = async (username, newRole) => {
    try {
      setChangingRole(username);
      await authApi.changeUserRole(username, newRole);
      const roleLabel = roles.find(r => r.value === newRole)?.label || newRole;
      showToast(`Роль пользователя ${username} успешно изменена на ${roleLabel}`);
      // Обновляем роль в локальном состоянии сразу
      setUserRoles(prev => ({ ...prev, [username]: newRole }));
      // Перезагружаем список пользователей
      const data = await userApi.getAllUsers();
      setUsers(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error("Failed to change user role:", e);
      setError(`Не удалось изменить роль: ${e.response?.data?.message || e.message}`);
    } finally {
      setChangingRole(null);
    }
  };

  useEffect(() => {
    const load = async () => {
      if (!isSuperUser) {
        setLoading(false);
        return;
      }
      try {
        const data = await userApi.getAllUsers();
        // Проверяем, что данные являются массивом
        const usersList = Array.isArray(data) ? data : [];
        setUsers(usersList);
        
        // Загружаем роли для всех пользователей
        const rolesMap = {};
        await Promise.all(
          usersList.map(async (user) => {
            try {
              const roleData = await authApi.getUserRole(user.username);
              rolesMap[user.username] = roleData.role;
            } catch (e) {
              console.error(`Failed to load role for user ${user.username}:`, e);
              rolesMap[user.username] = "USER"; // по умолчанию
            }
          })
        );
        setUserRoles(rolesMap);
      } catch (e) {
        console.error("Failed to load users:", e);
        setError("Не удалось загрузить пользователей");
        setUsers([]);
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
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Логин</th>
              <th>Email</th>
              <th>Имя</th>
              <th>Фамилия</th>
              <th>Роль</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.username}</td>
                <td>{u.email}</td>
                <td>{u.firstName || "-"}</td>
                <td>{u.lastName || "-"}</td>
                <td style={{ position: "relative" }}>
                  <div style={{ 
                    display: "flex", 
                    alignItems: "center", 
                    gap: "0.5rem",
                    width: "100%"
                  }}>
                    {/* Отображаем текущую роль */}
                    {userRoles[u.username] && (
                      <span style={{ 
                        padding: "0.25rem 0.5rem", 
                        borderRadius: "0.25rem",
                        fontSize: "0.85rem",
                        background: "rgba(59, 130, 246, 0.2)",
                        color: "#bfdbfe",
                        flexShrink: 0
                      }}>
                        {roles.find(r => r.value === userRoles[u.username])?.label || userRoles[u.username]}
                      </span>
                    )}
                    <select
                      className="select"
                      value={userRoles[u.username] || ""}
                      onChange={(e) => {
                        const newRole = e.target.value;
                        if (newRole && newRole !== userRoles[u.username]) {
                          handleRoleChange(u.username, newRole);
                        }
                      }}
                      disabled={changingRole === u.username || !userRoles[u.username]}
                      style={{ 
                        width: "180px",
                        marginLeft: "auto",
                        flexShrink: 0
                      }}
                    >
                      <option value={userRoles[u.username] || ""}>
                        {userRoles[u.username] ? "Изменить роль" : "Загрузка..."}
                      </option>
                      {roles.filter(r => r.value !== userRoles[u.username]).map((role) => (
                        <option key={role.value} value={role.value}>
                          {role.label}
                        </option>
                      ))}
                    </select>
                  </div>
                  {changingRole === u.username && (
                    <span style={{ marginLeft: "0.5rem", fontSize: "0.85rem", color: "#9ca3af" }}>
                      Изменение...
                    </span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <ToastContainer toasts={toasts} onClose={removeToast} />
    </div>
  );
}



