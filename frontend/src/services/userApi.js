import { api } from "./httpClient";

export const userApi = {
  async getCurrentUser() {
    const res = await api.get("/users/me");
    return res.data;
  },

  async getByUsername(username) {
    const res = await api.get(`/users/username/${username}`);
    return res.data;
  },

  async getAllUsers() {
    const res = await api.get("/users");
    return res.data;
  },

  async updateUserRole(id, payload) {
    // временно заглушка: бэкенд ещё не поддерживает изменение роли
    // этот метод будет использован, когда появится endpoint на SUPERUSER
    return payload;
  }
};



