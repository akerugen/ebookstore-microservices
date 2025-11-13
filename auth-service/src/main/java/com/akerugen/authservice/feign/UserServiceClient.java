package com.akerugen.authservice.feign;

import com.akerugen.authservice.feign.dto.CheckUserExistsResponse;
import com.akerugen.authservice.feign.dto.UserRequestDto;
import com.akerugen.authservice.feign.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Feign Client для общения с user-service.
 * Endpoints используемые auth-service:
 * - GET /api/users/check -> проверить существование пользователя
 * - POST /api/users/internal/create -> создать профиль после регистрации
 * - GET /api/users/username/{username} -> получить профиль по username (опционально)
 */
@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {

    /**
     * Проверить существует ли пользователь
     * GET /api/users/check?username=...&email=...
     *
     * @return CheckUserExistsResponse с полем "exists"
     */
    @GetMapping("/api/users/check")
    CheckUserExistsResponse checkUserExists(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    );


    /**
     * Получить профиль пользователя по username (опционально, для проверок)
     * GET /api/users/username/{username}
     *
     * @param username имя пользователя
     * @return UserResponseDto профиль пользователя
     */
    @GetMapping("/api/users/username/{username}")
    UserResponseDto getUserByUsername(@PathVariable("username") String username);

    /**
     * Создать профиль пользователя (internal endpoint)
     * POST /api/users/internal/create
     * Используется при регистрации
     *
     * @param request UserRequestDto без пароля
     * @return UserResponseDto профиль пользователя
     */
    @PostMapping("/api/users/internal/create")
    UserResponseDto createUser(@RequestBody UserRequestDto request);

}