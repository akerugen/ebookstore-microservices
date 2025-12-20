package com.akerugen.authservice.util;

import com.akerugen.authservice.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Утилита для проверки ролей пользователей из JWT токенов
 */
@Component
public class RoleAuthorizationUtil {

    private static final Logger logger = LogManager.getLogger(RoleAuthorizationUtil.class);
    private final JwtTokenProvider jwtTokenProvider;

    public RoleAuthorizationUtil(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Проверяет, имеет ли пользователь роль SUPER_USER
     * Извлекает роль из JWT токена в Authorization заголовке
     *
     * @param httpRequest HTTP запрос с заголовком Authorization
     * @return true если пользователь имеет роль SUPER_USER, иначе false
     */
    public boolean isSuperUser(HttpServletRequest httpRequest) {
        try {
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header in isSuperUser check");
                return false;
            }

            String token = authHeader.substring(7);
            String role = jwtTokenProvider.getRoleFromToken(token);

            logger.warn("User role from token in isSuperUser: {}", role);
            logger.warn("Checking if role equals ROLE_SUPER_USER: {}", role != null && role.equals("ROLE_SUPER_USER"));

            boolean isSuper = role != null && role.equals("ROLE_SUPER_USER");
            logger.warn("isSuperUser result: {}", isSuper);
            return isSuper;

        } catch (Exception ex) {
            logger.error("Error extracting role from token in isSuperUser: {}", ex.getMessage());
            logger.error("Exception details: ", ex);
            return false;
        }
    }

    /**
     * Проверяет, имеет ли пользователь роль ADMIN или SUPER_USER
     *
     * @param httpRequest HTTP запрос с заголовком Authorization
     * @return true если пользователь имеет роль ADMIN или SUPER_USER, иначе false
     */
    public boolean isAdminOrSuperUser(HttpServletRequest httpRequest) {
        try {
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                return false;
            }

            String token = authHeader.substring(7);
            String role = jwtTokenProvider.getRoleFromToken(token);

            logger.debug("User role from token: {}", role);

            return role != null &&
                    (role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPER_USER"));

        } catch (Exception ex) {
            logger.error("Error extracting role from token: {}", ex.getMessage());
            return false;
        }
    }
}

