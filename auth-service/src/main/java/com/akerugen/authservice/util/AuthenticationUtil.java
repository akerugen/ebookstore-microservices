package com.akerugen.authservice.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility класс для проверки аутентификации
 * Проверяет SecurityContext (заполненный JwtAuthenticationFilter)
 */
@Component
public class AuthenticationUtil {

    private static final Logger logger = LogManager.getLogger(AuthenticationUtil.class);

    /**
     * Проверяет есть ли аутентифицированный пользователь в SecurityContext
     * @return true если пользователь авторизован
     */
    public boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.debug("No authentication found in SecurityContext");
            return false;
        }

        if (!authentication.isAuthenticated()) {
            logger.debug("User is not authenticated");
            return false;
        }

        if (authentication.getPrincipal().equals("anonymousUser")) {
            logger.debug("User is anonymous");
            return false;
        }

        logger.debug("User is authenticated: {}", authentication.getName());
        return true;
    }

    /**
     * Получает username из SecurityContext
     * @return username если авторизован
     * @throws IllegalStateException если не авторизован
     */
    public String getCurrentUsername() {
        if (!isUserAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * Получает роль из SecurityContext
     * @return роль если авторизован
     * @throws IllegalStateException если не авторизован
     */
    public String getCurrentUserRole() {
        if (!isUserAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");
    }
}
