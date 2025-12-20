package akerugen.catalogservice.feign;

import akerugen.catalogservice.feign.dto.CreateNotificationRequestDto;
import akerugen.catalogservice.feign.dto.NotificationResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client для общения с notification-service.
 * Используется для создания уведомлений при изменении книг.
 */
@FeignClient(name = "notification-service", url = "${notification-service.url}")
public interface NotificationServiceClient {

    /**
     * Создать уведомление (internal endpoint)
     * POST /api/notifications/internal/create
     */
    @PostMapping("/api/notifications/internal/create")
    NotificationResponseDto createNotification(@RequestBody CreateNotificationRequestDto request);
}

