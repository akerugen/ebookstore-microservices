package com.akerugen.authservice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token Blacklist Service с использованием Redis
 * Хранит access tokens, которые были заблокированы при logout
 * TTL: время жизни access токена
 */
@Service
public class TokenBlacklistService {

    private static final Logger logger = LogManager.getLogger(TokenBlacklistService.class);
    private static final String BLACKLIST_PREFIX = "token_blacklist:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public TokenBlacklistService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Добавляет токен в чёрный список
     */
    public void blacklistToken(String token, long expirationTimeMs) {
        try {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "blacklisted", expirationTimeMs, TimeUnit.MILLISECONDS);
            logger.info("Token added to blacklist, TTL: {}ms", expirationTimeMs);

        } catch (Exception ex) {
            logger.error("Failed to blacklist token: {}", ex.getMessage());
            throw new RuntimeException("Failed to blacklist token", ex);
        }
    }

    /**
     * Проверяет находится ли токен в чёрном списке
     */
    public boolean isTokenBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            Boolean exists = redisTemplate.hasKey(key);

            if (Boolean.TRUE.equals(exists)) {
                logger.debug("Token is blacklisted");
                return true;
            }

            return false;

        } catch (Exception ex) {
            logger.error("Error checking blacklist: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Получить размер blacklist
     */
    public long getBlacklistSize() {
        try {
            // Подсчитываем ключи с префиксом
            return redisTemplate.keys(BLACKLIST_PREFIX + "*").size();
        } catch (Exception ex) {
            logger.error("Error getting blacklist size: {}", ex.getMessage());
            return 0;
        }
    }
}
