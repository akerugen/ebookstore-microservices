import React, { createContext, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { authApi } from "../services/authApi";
import { decodeJwtPayload } from "../utils/jwt";
import {
  setHttpAccessToken,
  setHttpRefreshToken,
  setTokenUpdateCallback,
  setUnauthorizedCallback
} from "../services/httpClient";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [accessToken, setAccessToken] = useState(null);
  const [refreshToken, setRefreshToken] = useState(null);
  const [userInfo, setUserInfo] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const storedAccess = localStorage.getItem("accessToken");
    const storedRefresh = localStorage.getItem("refreshToken");
    if (storedAccess) {
      const payload = decodeJwtPayload(storedAccess);
      setAccessToken(storedAccess);
      setRefreshToken(storedRefresh);
      setUserInfo(payload);
      // устанавливаем токены в httpClient
      setHttpAccessToken(storedAccess);
      if (storedRefresh) {
        setHttpRefreshToken(storedRefresh);
      }
    }

    // регистрируем колбэк для обновления токенов при refresh
    setTokenUpdateCallback((newAccessToken, newRefreshToken) => {
      setAccessToken(newAccessToken);
      if (newRefreshToken) {
        setRefreshToken(newRefreshToken);
      }
      const payload = decodeJwtPayload(newAccessToken);
      setUserInfo(payload);
    });

    // регистрируем колбэк для автоматического logout при 401
    setUnauthorizedCallback(() => {
      handleUnauthorized();
    });
  }, []);

  const handleUnauthorized = async () => {
    // при 401 делаем logout и редирект на login
    const storedRefresh = refreshToken || localStorage.getItem("refreshToken");
    if (storedRefresh) {
      try {
        await authApi.logout(storedRefresh);
      } catch (e) {
        // игнорируем ошибку логаута
      }
    }
    setAccessToken(null);
    setRefreshToken(null);
    setUserInfo(null);
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    navigate("/login");
  };

  const login = async (usernameOrEmail, password) => {
    const response = await authApi.login({ usernameOrEmail, password });
    const { accessToken: access, refreshToken: refresh } = response;
    const payload = decodeJwtPayload(access);

    setAccessToken(access);
    setRefreshToken(refresh);
    setUserInfo(payload);

    localStorage.setItem("accessToken", access);
    localStorage.setItem("refreshToken", refresh);
    // устанавливаем токены в httpClient
    setHttpAccessToken(access);
    setHttpRefreshToken(refresh);
  };

  const register = async (data) => {
    // регистрация сразу создаёт access/refresh токены, как и логин
    const response = await authApi.register(data);
    const { accessToken: access, refreshToken: refresh } = response;
    const payload = decodeJwtPayload(access);

    setAccessToken(access);
    setRefreshToken(refresh);
    setUserInfo(payload);

    localStorage.setItem("accessToken", access);
    localStorage.setItem("refreshToken", refresh);
    // устанавливаем токены в httpClient
    setHttpAccessToken(access);
    setHttpRefreshToken(refresh);
  };

  const logout = async () => {
    const storedRefresh = refreshToken || localStorage.getItem("refreshToken");
    if (storedRefresh) {
      try {
        await authApi.logout(storedRefresh);
      } catch (e) {
        // игнорируем ошибку логаута
      }
    }
    setAccessToken(null);
    setRefreshToken(null);
    setUserInfo(null);
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  };

  const value = {
    accessToken,
    refreshToken,
    userInfo,
    isAuthenticated: Boolean(accessToken),
    login,
    register,
    logout
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return ctx;
}



