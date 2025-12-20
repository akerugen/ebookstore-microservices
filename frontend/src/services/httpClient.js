import axios from "axios";
import { decodeJwtPayload } from "../utils/jwt";
import { authApi } from "./authApi";

// Определяем baseURL в зависимости от окружения
// В dev режиме (Vite) используем относительный путь для proxy
// В production (Docker/K8s) используем либо переменную окружения, либо относительный путь
const getBaseURL = () => {
  // Проверяем, запущено ли через Vite dev server
  // import.meta.env.DEV = true только в dev режиме Vite
  if (import.meta.env.DEV) {
    return "/api";
  }
  // В production используем переменную окружения если она задана, иначе относительный путь
  // Для docker-compose можно задать VITE_API_BASE_URL=http://localhost/api
  // Для k8s используем относительный путь (через Ingress все идет на один домен)
  return import.meta.env.VITE_API_BASE_URL || "/api";
};

const api = axios.create({
  baseURL: getBaseURL()
});

// простой хранилище токена для axios
let accessTokenMemo = null;
let refreshTokenMemo = null;
// колбэки для обновления токена и logout
let onTokenUpdateCallback = null;
let onUnauthorizedCallback = null;
// флаг для предотвращения множественных попыток refresh
let isRefreshing = false;
let failedQueue = [];

export function setHttpAccessToken(token) {
  accessTokenMemo = token;
}

export function setHttpRefreshToken(token) {
  refreshTokenMemo = token;
}

export function setTokenUpdateCallback(callback) {
  onTokenUpdateCallback = callback;
}

export function setUnauthorizedCallback(callback) {
  onUnauthorizedCallback = callback;
}

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

api.interceptors.request.use((config) => {
  if (accessTokenMemo) {
    config.headers.Authorization = `Bearer ${accessTokenMemo}`;
  }
  return config;
});

// обработка 401 - автоматическое обновление токена или logout
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // если получили 401 и это не запрос на refresh или logout
    if (error.response?.status === 401 && !originalRequest._retry) {
      // если уже идет процесс обновления токена, добавляем запрос в очередь
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return api(originalRequest);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      // пытаемся обновить токен через refresh token
      const refreshToken = refreshTokenMemo || localStorage.getItem("refreshToken");
      if (refreshToken) {
        try {
          const response = await authApi.refreshToken(refreshToken);
          const newAccessToken = response.accessToken;
          const newRefreshToken = response.refreshToken;

          // обновляем токены
          accessTokenMemo = newAccessToken;
          if (newRefreshToken) {
            refreshTokenMemo = newRefreshToken;
          }

          // обновляем токены в localStorage и контексте
          localStorage.setItem("accessToken", newAccessToken);
          if (newRefreshToken) {
            localStorage.setItem("refreshToken", newRefreshToken);
          }

          if (onTokenUpdateCallback) {
            onTokenUpdateCallback(newAccessToken, newRefreshToken);
          }

          // обновляем заголовок и повторяем запрос
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          processQueue(null, newAccessToken);
          isRefreshing = false;
          return api(originalRequest);
        } catch (refreshError) {
          // refresh token тоже истёк или невалиден - делаем logout
          processQueue(refreshError, null);
          isRefreshing = false;
          if (onUnauthorizedCallback) {
            onUnauthorizedCallback();
          }
          return Promise.reject(refreshError);
        }
      } else {
        // нет refresh token - делаем logout
        isRefreshing = false;
        if (onUnauthorizedCallback) {
          onUnauthorizedCallback();
        }
        return Promise.reject(error);
      }
    }

    return Promise.reject(error);
  }
);

export { api };



