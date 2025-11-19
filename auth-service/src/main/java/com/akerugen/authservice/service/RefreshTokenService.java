package com.akerugen.authservice.service;

import com.akerugen.authservice.exception.TokenException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Сервис для управления refresh tokens
 */
@Service
public class RefreshTokenService {

    private static final Logger logger = LogManager.getLogger(RefreshTokenService.class);

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String ACTIVE_SESSION_PREFIX = "active_session:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RefreshTokenService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Сохраняет refresh token в Redis
     *
     * @param token JWT refresh token
     * @param username имя пользователя
     * @param expirationTimeMs время жизни в миллисекундах
     */
    public void storeRefreshToken(String token, String username, long expirationTimeMs) {
        try {
            // Ключ: refresh_token:{token}
            String tokenKey = REFRESH_TOKEN_PREFIX + token;
            redisTemplate.opsForValue().set(tokenKey, username, expirationTimeMs, TimeUnit.MILLISECONDS);

            // Ключ: active_session:{username} -> token
            String sessionKey = ACTIVE_SESSION_PREFIX + username;
            redisTemplate.opsForValue().set(sessionKey, token, expirationTimeMs, TimeUnit.MILLISECONDS);

            logger.info("Refresh token stored for user: {}, TTL: {}ms", username, expirationTimeMs);

        } catch (Exception ex) {
            logger.error("Failed to store refresh token: {}", ex.getMessage());
            throw new RuntimeException("Failed to store refresh token", ex);
        }
    }

    /**
     * Проверяет есть ли активная сессия у пользователя
     */
    public boolean hasActiveSession(String username) {
        try {
            String sessionKey = ACTIVE_SESSION_PREFIX + username;
            Boolean exists = redisTemplate.hasKey(sessionKey);

            if (Boolean.TRUE.equals(exists)) {
                logger.debug("Active session found for user: {}", username);
                return true;
            }

            logger.debug("No active session for user: {}", username);
            return false;

        } catch (Exception ex) {
            logger.error("Error checking active session: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Проверяет валиден ли refresh token
     */
    public boolean isRefreshTokenValid(String token) {
        try {
            String tokenKey = REFRESH_TOKEN_PREFIX + token;
            Object username = redisTemplate.opsForValue().get(tokenKey);

            if (username == null) {
                logger.warn("Refresh token not found or expired");
                throw new TokenException("Refresh token is invalid or has been revoked");
            }

            return true;

        } catch (Exception ex) {
            logger.error("Error validating refresh token: {}", ex.getMessage());
            throw new TokenException("Failed to validate refresh token");
        }
    }

    /**
     * Отозвать все refresh tokens пользователя (для logout)
     */
    public void revokeAllUserTokens(String token) {
        try {
            String tokenKey = REFRESH_TOKEN_PREFIX + token;
            Object username = redisTemplate.opsForValue().get(tokenKey);

            if (username != null) {
                String sessionKey = ACTIVE_SESSION_PREFIX + username.toString();
                redisTemplate.delete(tokenKey);
                redisTemplate.delete(sessionKey);
                logger.info("All refresh tokens revoked for user: {}", username);
            } else {
                redisTemplate.delete(tokenKey);
                logger.warn("Attempted to revoke unknown token");
            }

        } catch (Exception ex) {
            logger.error("Error revoking tokens: {}", ex.getMessage());
            throw new RuntimeException("Failed to revoke tokens", ex);
        }
    }

    /**
     * Получить username из token'а (используется для logout)
     */
    public String getUsernameFromToken(String token) {
        try {
            String tokenKey = REFRESH_TOKEN_PREFIX + token;
            Object username = redisTemplate.opsForValue().get(tokenKey);

            if (username == null) {
                throw new TokenException("Token not found");
            }

            return username.toString();

        } catch (Exception ex) {
            logger.error("Error getting username from token: {}", ex.getMessage());
            throw new TokenException("Failed to get username from token");
        }
    }
}
