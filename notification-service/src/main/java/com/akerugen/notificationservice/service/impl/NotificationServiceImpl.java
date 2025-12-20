package com.akerugen.notificationservice.service.impl;

import com.akerugen.notificationservice.dto.request.CreateNotificationRequest;
import com.akerugen.notificationservice.dto.response.NotificationResponseDto;
import com.akerugen.notificationservice.dto.response.UnreadCountResponse;
import com.akerugen.notificationservice.entity.Notification;
import com.akerugen.notificationservice.mapper.NotificationMapper;
import com.akerugen.notificationservice.repo.NotificationRepository;
import com.akerugen.notificationservice.service.NotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public NotificationResponseDto createNotification(CreateNotificationRequest request) {
        logger.info("Creating notification for user {} with type: {}", request.getUserId(), request.getType());
        Notification notification = notificationMapper.toEntity(request);
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Notification created with id: {}", savedNotification.getId());
        return notificationMapper.toResponseDto(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUserNotifications(Long userId) {
        logger.info("Fetching notifications for user: {}", userId);
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(Long userId) {
        logger.debug("Getting unread count for user: {}", userId);
        long count = notificationRepository.countByUserIdAndIsReadFalse(userId);
        return new UnreadCountResponse(count);
    }

    @Override
    public NotificationResponseDto markAsRead(Long notificationId, Long userId) {
        logger.info("Marking notification {} as read for user: {}", notificationId, userId);
        
        if (!notificationRepository.existsByIdAndUserId(notificationId, userId)) {
            throw new RuntimeException("Notification not found or access denied");
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        logger.info("Notification {} marked as read", notificationId);
        return notificationMapper.toResponseDto(updatedNotification);
    }

    @Override
    public void createNotificationForAllUsers(CreateNotificationRequest request, List<Long> excludeUserIds) {
        // В данной реализации мы создаем уведомления только для конкретного пользователя
        // Пока что этот метод не используется, но оставлен для будущей реализации
        logger.info("createNotificationForAllUsers called with type: {}", request.getType());
        // TODO: реализовать через Feign клиент к user-service для получения всех userId
        throw new UnsupportedOperationException("Method not yet implemented");
    }
}

