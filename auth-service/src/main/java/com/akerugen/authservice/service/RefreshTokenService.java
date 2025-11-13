package com.akerugen.authservice.service;

import com.akerugen.authservice.exception.TokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    // TODO: вместо хранения в памяти лучше потом будет Redis бахнуть
    private final ConcurrentHashMap<String, RefreshTokenData> tokenStore = new ConcurrentHashMap<>();

    /**
     * Сохраняет refresh token
     * @param token JWT refresh token
     * @param expirationTime время жизни в миллисекундах
     */
    public void storeRefreshToken(String token, Long expirationTime) {
        RefreshTokenData data = new RefreshTokenData(System.currentTimeMillis() + expirationTime);
        tokenStore.put(token, data);
        logger.info("Refresh token stored, expiration time: {}ms", expirationTime);
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
            logger.warn("Refresh token has expired");
            throw new TokenException("Refresh token has expired");
        }

        return true;
    }

    /**
     * Отозвать refresh token
     */
    public void revokeRefreshToken(String token) {
        tokenStore.remove(token);
        logger.info("Refresh token revoked");
    }

    /**
     * Отозвать все refresh tokens (для logout)
     */
    public void revokeAllUserTokens(String token) {
        // Т.к. token содержит информацию о пользователе (username, role)
        // При logout мы просто удаляем этот токен
        tokenStore.remove(token);
        logger.info("User refresh token revoked");
    }

    /**
     * Внутренний класс для хранения данных refresh token
     */
    private static class RefreshTokenData {
        Long expirationTime;

        RefreshTokenData(Long expirationTime) {
            this.expirationTime = expirationTime;
        }
    }
}