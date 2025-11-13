package com.akerugen.authservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {

    /**
     * Проверяет, авторизован ли текущий пользователь в системе
     *
     * @return true если пользователь авторизован (имеет валидный JWT в SecurityContext)
     *         false если не авторизован или является anonymousUser
     */
    public boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser");
    }

    /**
     * Получает имя (username) текущего авторизованного пользователя
     *
     * @return username если авторизован
     * @throws IllegalStateException если не авторизован
     */
    public String getCurrentUsername() {
        if (!isUserAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Получает роль текущего авторизованного пользователя
     * (Это требует чтобы роль была добавлена в Security)
     *
     * @return роль пользователя
     */
    public String getCurrentUserRole() {
        if (!isUserAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");
    }
}