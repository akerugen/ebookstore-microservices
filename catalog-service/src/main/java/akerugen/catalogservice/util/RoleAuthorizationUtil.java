package akerugen.catalogservice.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleAuthorizationUtil {

    private static final Logger logger = LogManager.getLogger(RoleAuthorizationUtil.class);
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public RoleAuthorizationUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Проверяет, имеет ли пользователь роль ADMIN или SUPER_USER
     * Извлекает роль из JWT токена в Authorization заголовке
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
            String role = jwtTokenUtil.getRoleFromToken(token);

            logger.debug("User role from token: {}", role);

            // Проверяем, есть ли роль ROLE_ADMIN или ROLE_SUPER_USER
            return role != null &&
                    (role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPER_USER"));

        } catch (Exception ex) {
            logger.error("Error extracting role from token: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Проверяет, имеет ли пользователь конкретную роль
     *
     * @param httpRequest HTTP запрос с заголовком Authorization
     * @param requiredRole требуемая роль (например, "ROLE_ADMIN")
     * @return true если пользователь имеет указанную роль, иначе false
     */
    public boolean hasRole(HttpServletRequest httpRequest, String requiredRole) {
        try {
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                return false;
            }

            String token = authHeader.substring(7);
            String role = jwtTokenUtil.getRoleFromToken(token);

            logger.debug("User role from token: {}", role);

            return role != null && role.equals(requiredRole);

        } catch (Exception ex) {
            logger.error("Error extracting role from token: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Извлекает роль пользователя из токена
     *
     * @param httpRequest HTTP запрос с заголовком Authorization
     * @return роль пользователя или null если токен невалиден
     */
    public String getUserRole(HttpServletRequest httpRequest) {
        try {
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                return null;
            }

            String token = authHeader.substring(7);
            return jwtTokenUtil.getRoleFromToken(token);

        } catch (Exception ex) {
            logger.error("Error extracting role from token: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * Извлекает username пользователя из токена
     *
     * @param httpRequest HTTP запрос с заголовком Authorization
     * @return username пользователя или null если токен невалиден
     */
    public String getUsername(HttpServletRequest httpRequest) {
        try {
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                return null;
            }

            String token = authHeader.substring(7);
            return jwtTokenUtil.getUsernameFromToken(token);

        } catch (Exception ex) {
            logger.error("Error extracting username from token: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * Извлекает userId пользователя из токена
     *
     * @param httpRequest HTTP запрос с заголовком Authorization
     * @return userId пользователя или null если токен невалиден
     */
    public Long getUserId(HttpServletRequest httpRequest) {
        try {
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                return null;
            }

            String token = authHeader.substring(7);
            return jwtTokenUtil.getUserIdFromToken(token);

        } catch (Exception ex) {
            logger.error("Error extracting userId from token: {}", ex.getMessage());
            return null;
        }
    }
}