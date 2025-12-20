package com.akerugen.notificationservice.repo;

import com.akerugen.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Получить все уведомления пользователя, отсортированные по дате создания (новые сначала)
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Получить количество непрочитанных уведомлений пользователя
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * Получить все непрочитанные уведомления пользователя
     */
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    /**
     * Проверить существование уведомления для пользователя
     */
    boolean existsByIdAndUserId(Long id, Long userId);
}

