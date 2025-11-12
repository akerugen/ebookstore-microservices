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

    public void storeRefreshToken(String token, Long userId, Long expirationTime) {
        RefreshTokenData data = new RefreshTokenData(userId, System.currentTimeMillis() + expirationTime);
        tokenStore.put(token, data);
        logger.info("Refresh token stored for user: {}", userId);
    }

    public boolean isRefreshTokenValid(String token) {
        RefreshTokenData data = tokenStore.get(token);
        if (data == null) {
            logger.warn("Refresh token not found");
            throw new TokenException("Refresh token is invalid");
        }

        if (data.expirationTime < System.currentTimeMillis()) {
            tokenStore.remove(token);
            logger.warn("Refresh token has expired");
            throw new TokenException("Refresh token has expired");
        }

        return true;
    }

    public Long getUserIdFromRefreshToken(String token) {
        if (!isRefreshTokenValid(token)) {
            throw new TokenException("Invalid refresh token");
        }
        return tokenStore.get(token).userId;
    }

    public void revokeRefreshToken(String token) {
        tokenStore.remove(token);
        logger.info("Refresh token revoked");
    }

    public void revokeAllUserTokens(Long userId) {
        tokenStore.entrySet().removeIf(entry -> entry.getValue().userId.equals(userId));
        logger.info("All refresh tokens revoked for user: {}", userId);
    }

    private static class RefreshTokenData {
        Long userId;
        Long expirationTime;

        RefreshTokenData(Long userId, Long expirationTime) {
            this.userId = userId;
            this.expirationTime = expirationTime;
        }
    }
}
