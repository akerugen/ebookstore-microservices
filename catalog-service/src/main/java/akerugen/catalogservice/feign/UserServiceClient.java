package akerugen.catalogservice.feign;

import akerugen.catalogservice.feign.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Feign Client для общения с user-service.
 * Используется для получения списка всех пользователей для создания уведомлений.
 */
@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {

    /**
     * Получить всех пользователей
     * GET /api/users
     */
    @GetMapping("/api/users")
    List<UserResponseDto> getAllUsers();
}

