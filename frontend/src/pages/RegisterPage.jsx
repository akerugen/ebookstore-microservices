import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: "",
    email: "",
    firstName: "",
    lastName: "",
    password: "",
    confirmPassword: ""
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const onChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (form.password !== form.confirmPassword) {
      setError("пароли не совпадают");
      return;
    }

    setLoading(true);
    try {
      await register({
        username: form.username,
        email: form.email,
        firstName: form.firstName,
        lastName: form.lastName,
        password: form.password,
        confirmPassword: form.confirmPassword
      });
      // после успешной регистрации пользователь уже авторизован
      navigate("/catalog");
    } catch (err) {
      setError("не удалось зарегистрироваться. проверьте данные.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="card">
        <h1>Регистрация</h1>
        <form onSubmit={onSubmit} className="form">
          <label>
            Логин
            <input
              type="text"
              name="username"
              value={form.username}
              onChange={onChange}
              required
            />
          </label>
          <label>
            Имя
            <input
              type="text"
              name="firstName"
              value={form.firstName}
              onChange={onChange}
            />
          </label>
          <label>
            Фамилия
            <input
              type="text"
              name="lastName"
              value={form.lastName}
              onChange={onChange}
            />
          </label>
          <label>
            Email
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={onChange}
              required
            />
          </label>
          <label>
            Пароль
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={onChange}
              required
            />
          </label>
          <label>
            Повторите пароль
            <input
              type="password"
              name="confirmPassword"
              value={form.confirmPassword}
              onChange={onChange}
              required
            />
          </label>
          {error && <div className="error-text">{error}</div>}
          <button className="btn primary" type="submit" disabled={loading}>
            {loading ? "Создаём..." : "Зарегистрироваться"}
          </button>
        </form>
        <div className="muted">
          Уже есть аккаунт? <Link to="/login">Войти</Link>
        </div>
      </div>
    </div>
  );
}



