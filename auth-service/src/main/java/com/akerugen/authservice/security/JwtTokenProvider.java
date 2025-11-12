package com.akerugen.authservice.security;

import com.akerugen.authservice.exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider для создания и валидации токенов
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LogManager.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * Получаем SecretKey для подписи токенов
     * Минимум 256 бит для HS512 (32 байта)
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Генерит access token
     * @param userId ID пользователя
     * @param username имя пользователя
     * @return JWT access token строка
     */
    public String generateAccessToken(Long userId, String username) {
        logger.debug("Generating access token for user: {}", username);
        return generateToken(userId, username, accessTokenExpiration, "ACCESS");
    }

    /**
     * Генерит refresh token
     * @param userId ID пользователя
     * @param username имя пользователя
     * @return JWT refresh token строка
     */
    public String generateRefreshToken(Long userId, String username) {
        logger.debug("Generating refresh token for user: {}", username);
        return generateToken(userId, username, refreshTokenExpiration, "REFRESH");
    }

    /**
     * Внутренний метод для генерации любого типа токена
     * @param userId ID пользователя
     * @param username имя пользователя
     * @param expirationTime время жизни токена в миллисекундах
     * @param tokenType тип токена (ACCESS или REFRESH)
     * @return JWT токен строка
     */
    private String generateToken(Long userId, String username, long expirationTime, String tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("username", username)
                .claim("tokenType", tokenType)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Извлекает userId из токена
     * @param token JWT токен
     * @return ID пользователя
     * @throws TokenException если токен невалиден
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            Object userIdClaim = claims.get("userId");

            if (userIdClaim instanceof Integer) {
                return ((Integer) userIdClaim).longValue();
            }

            return (Long) userIdClaim;
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("Failed to get userId from token: {}", ex.getMessage());
            throw new TokenException("Invalid token");
        }
    }

    /**
     * Извлекает username из токена
     * @param token JWT токен
     * @return имя пользователя
     * @throws TokenException если токен невалиден
     */
    public String getUsernameFromToken(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("Failed to get username from token: {}", ex.getMessage());
            throw new TokenException("Invalid token");
        }
    }

    /**
     * Извлекает тип токена (ACCESS или REFRESH)
     * @param token JWT токен
     * @return тип токена
     * @throws TokenException если токен невалиден
     */
    public String getTokenType(String token) {
        try {
            return (String) getClaims(token).get("tokenType");
        } catch (JwtException ex) {
            logger.error("Failed to get token type: {}", ex.getMessage());
            throw new TokenException("Invalid token");
        }
    }

    /**
     * Валидирует токен
     * Проверяет подпись, срок действия и структуру
     * @param token JWT токен
     * @return true если токен валиден
     * @throws TokenException если токен невалиден
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            logger.debug("Token validated successfully");
            return true;
        } catch (ExpiredJwtException ex) {
            logger.warn("JWT token is expired: {}", ex.getMessage());
            throw new TokenException("Token has expired");
        } catch (UnsupportedJwtException ex) {
            logger.warn("JWT token is unsupported: {}", ex.getMessage());
            throw new TokenException("Unsupported token");
        } catch (MalformedJwtException ex) {
            logger.warn("Invalid JWT token: {}", ex.getMessage());
            throw new TokenException("Malformed token");
        } catch (SignatureException ex) {
            logger.warn("JWT signature validation failed: {}", ex.getMessage());
            throw new TokenException("Invalid token signature");
        } catch (IllegalArgumentException ex) {
            logger.warn("JWT claims string is empty: {}", ex.getMessage());
            throw new TokenException("Empty token");
        }
    }

    /**
     * Парсит и возвращает Claims из токена
     * @param token JWT токен
     * @return Claims объект с информацией из токена
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Получает время до истечения токена (в миллисекундах)
     * @param token JWT токен
     * @return миллисекунды до истечения (может быть отрицательным если истек)
     */
    public Long getTokenExpirationTime(String token) {
        try {
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            long timeRemaining = expiration.getTime() - System.currentTimeMillis();
            logger.debug("Token expiration time remaining: {} ms", timeRemaining);
            return timeRemaining;
        } catch (JwtException ex) {
            logger.error("Failed to get expiration time: {}", ex.getMessage());
            return 0L;
        }
    }

    /**
     * Проверяет, истек ли токен
     * @param token JWT токен
     * @return true если токен истек, false если еще действует
     */
    public boolean isTokenExpired(String token) {
        try {
            getClaims(token);
            return false;
        } catch (ExpiredJwtException ex) {
            logger.debug("Token has expired");
            return true;
        }
    }
}
