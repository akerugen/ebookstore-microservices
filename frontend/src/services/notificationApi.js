import { api } from "./httpClient";

export const notificationApi = {
  /**
   * Получить все уведомления текущего пользователя
   */
  getNotifications: async () => {
    const response = await api.get("/notifications");
    return response.data;
  },

  /**
   * Получить количество непрочитанных уведомлений
   */
  getUnreadCount: async () => {
    const response = await api.get("/notifications/unread/count");
    return response.data.count;
  },

  /**
   * Отметить уведомление как прочитанное
   */
  markAsRead: async (notificationId) => {
    const response = await api.patch(`/notifications/${notificationId}/read`);
    return response.data;
  },
};

