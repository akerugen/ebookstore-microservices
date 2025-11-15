package com.akerugen.authservice.service;

import com.akerugen.authservice.exception.TokenException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для управления refresh tokens
 * TODO: потом мапы заменить на Redis
 */
@Service
public class RefreshTokenService {

    private static final Logger logger = LogManager.getLogger(RefreshTokenService.class);

    // token -> RefreshTokenData
    private final ConcurrentHashMap<String, RefreshTokenData> tokenStore = new ConcurrentHashMap<>();

    // username -> refresh token (для проверки активных сессий)
    private final ConcurrentHashMap<String, String> activeUserSessions = new ConcurrentHashMap<>();

    /**
     * Сохраняет refresh token
     */
    public void storeRefreshToken(String token, String username, Long expirationTime) {
        RefreshTokenData data = new RefreshTokenData(username, System.currentTimeMillis() + expirationTime);
        tokenStore.put(token, data);

        // Сохраняем активную сессию пользователя
        activeUserSessions.put(username, token);

        logger.info("Refresh token stored for user: {}, expiration: {}ms", username, expirationTime);
    }

    /**
     * Проверяет есть ли активная сессия у пользователя
     * @param username имя пользователя
     * @return true если есть активный refresh token
     */
    public boolean hasActiveSession(String username) {
        String activeToken = activeUserSessions.get(username);

        if (activeToken == null) {
            logger.debug("No active session found for user: {}", username);
            return false;
        }

        // Проверяем что токен всё ещё валиден
        RefreshTokenData data = tokenStore.get(activeToken);
        if (data == null || data.expirationTime < System.currentTimeMillis()) {
            // Токен истёк, удаляем
            activeUserSessions.remove(username);
            if (data != null) {
                tokenStore.remove(activeToken);
            }
            logger.debug("Active session expired for user: {}", username);
            return false;
        }

        logger.debug("Active session found for user: {}", username);
        return true;
    }

    /**
     * Проверяет валиден ли refresh token
     */
    public boolean isRefreshTokenValid(String token) {
        RefreshTokenData data = tokenStore.get(token);

        if (data == null) {
            logger.warn("Refresh token not found in store");
            throw new TokenException("Refresh token is invalid or has been revoked");
        }

        if (data.expirationTime < System.currentTimeMillis()) {
            tokenStore.remove(token);
            activeUserSessions.remove(data.username);
            logger.warn("Refresh token has expired for user: {}", data.username);
            throw new TokenException("Refresh token has expired");
        }

        return true;
    }

    /**
     * Отозвать refresh token
     */
    public void revokeRefreshToken(String token) {
        RefreshTokenData data = tokenStore.remove(token);
        if (data != null) {
            activeUserSessions.remove(data.username);
            logger.info("Refresh token revoked for user: {}", data.username);
        }
    }

    /**
     * Отозвать все refresh tokens пользователя (для logout)
     */
    public void revokeAllUserTokens(String token) {
        RefreshTokenData data = tokenStore.get(token);

        if (data != null) {
            String username = data.username;

            // Удаляем все токены этого пользователя
            tokenStore.entrySet().removeIf(entry ->
                    entry.getValue().username.equals(username)
            );

            activeUserSessions.remove(username);
            logger.info("All refresh tokens revoked for user: {}", username);
        } else {
            tokenStore.remove(token);
            logger.warn("Attempted to revoke unknown token");
        }
    }

    /**
     * Внутренний класс для хранения данных refresh token
     */
    private static class RefreshTokenData {
        String username;
        Long expirationTime;

        RefreshTokenData(String username, Long expirationTime) {
            this.username = username;
            this.expirationTime = expirationTime;
        }
    }
}
