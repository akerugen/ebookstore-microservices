import { api } from "./httpClient";

export const authApi = {
  async login(payload) {
    const response = await api.post("/auth/login", payload);
    return response.data;
  },

  async register(payload) {
    const response = await api.post("/auth/register", payload);
    return response.data;
  },

  async logout(refreshToken) {
    await api.post("/auth/logout", { refreshToken });
  },

  async refreshToken(refreshToken) {
    const response = await api.post("/auth/refresh", { refreshToken });
    return response.data;
  }
};



