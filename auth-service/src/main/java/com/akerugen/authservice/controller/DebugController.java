package com.akerugen.authservice.controller;

import com.akerugen.authservice.service.TokenBlacklistService;
import com.akerugen.authservice.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Debug endpoint для проверки Redis состояния
 * TODO: убрать в продакшене
 */
@RestController
@RequestMapping("/api/debug")
@Tag(name = "Debug", description = "Debug endpoints - только для разработки")
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public DebugController(TokenBlacklistService tokenBlacklistService,
                           RefreshTokenService refreshTokenService,
                           RedisTemplate<String, Object> redisTemplate) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.refreshTokenService = refreshTokenService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Получить статистику Redis
     */
    @GetMapping("/redis-stats")
    @Operation(summary = "Get Redis statistics", description = "Debug endpoint to check Redis state")
    public ResponseEntity<Map<String, Object>> getRedisStats() {
        logger.info("Debug: Getting Redis statistics");

        Map<String, Object> stats = new HashMap<>();

        try {
            // 1. Blacklist size
            long blacklistSize = tokenBlacklistService.getBlacklistSize();
            stats.put("blacklist_size", blacklistSize);

            // 2. Все ключи в Redis
            Set<String> allKeys = redisTemplate.keys("*");
            stats.put("total_keys", allKeys != null ? allKeys.size() : 0);

            // 3. Детали по типам ключей
            Map<String, Integer> keyStats = new HashMap<>();

            if (allKeys != null) {
                for (String key : allKeys) {
                    if (key.startsWith("token_blacklist:")) {
                        keyStats.put("blacklist", keyStats.getOrDefault("blacklist", 0) + 1);
                    } else if (key.startsWith("refresh_token:")) {
                        keyStats.put("refresh_tokens", keyStats.getOrDefault("refresh_tokens", 0) + 1);
                    } else if (key.startsWith("active_session:")) {
                        keyStats.put("active_sessions", keyStats.getOrDefault("active_sessions", 0) + 1);
                    } else {
                        keyStats.put("other", keyStats.getOrDefault("other", 0) + 1);
                    }
                }
            }

            stats.put("key_breakdown", keyStats);

            // 4. Примеры ключей
            if (allKeys != null) {
                Map<String, List<String>> examples = new HashMap<>();
                examples.put("blacklist", new ArrayList<>());
                examples.put("refresh_tokens", new ArrayList<>());
                examples.put("active_sessions", new ArrayList<>());

                for (String key : allKeys) {
                    if (key.startsWith("token_blacklist:")) {
                        examples.get("blacklist").add(key);
                    } else if (key.startsWith("refresh_token:")) {
                        examples.get("refresh_tokens").add(key);
                    } else if (key.startsWith("active_session:")) {
                        examples.get("active_sessions").add(key);
                    }
                }

                stats.put("example_keys", examples);
            }

            logger.info("Redis stats: {}", stats);
            return ResponseEntity.ok(stats);

        } catch (Exception ex) {
            logger.error("Error getting Redis stats: {}", ex.getMessage());
            stats.put("error", ex.getMessage());
            return ResponseEntity.ok(stats);
        }
    }

    /**
     * Очистить ВСЕ данные из Redis
     */
    @GetMapping("/redis-clear")
    @Operation(summary = "Clear all Redis data", description = "⚠️ DANGEROUS - debug only!")
    public ResponseEntity<Map<String, String>> clearRedis() {
        logger.warn("DEBUG: Clearing ALL Redis data!");

        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "All Redis data cleared");
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            logger.error("Error clearing Redis: {}", ex.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", ex.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
