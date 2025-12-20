package com.akerugen.notificationservice.service;

import com.akerugen.notificationservice.dto.request.CreateNotificationRequest;
import com.akerugen.notificationservice.dto.response.NotificationResponseDto;
import com.akerugen.notificationservice.dto.response.UnreadCountResponse;

import java.util.List;

public interface NotificationService {

    /**
     * Создать уведомление для пользователя
     */
    NotificationResponseDto createNotification(CreateNotificationRequest request);

    /**
     * Получить все уведомления пользователя
     */
    List<NotificationResponseDto> getUserNotifications(Long userId);

    /**
     * Получить количество непрочитанных уведомлений пользователя
     */
    UnreadCountResponse getUnreadCount(Long userId);

    /**
     * Отметить уведомление как прочитанное
     */
    NotificationResponseDto markAsRead(Long notificationId, Long userId);

    /**
     * Создать уведомления для всех пользователей (кроме администраторов)
     * Используется при создании/обновлении/удалении книги
     */
    void createNotificationForAllUsers(CreateNotificationRequest request, List<Long> excludeUserIds);
}

