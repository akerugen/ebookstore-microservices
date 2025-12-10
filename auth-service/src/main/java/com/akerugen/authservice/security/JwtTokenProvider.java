package com.akerugen.authservice.security;

import com.akerugen.authservice.exception.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Token Provider
 * Генерирует, валидирует и извлекает информацию из JWT токенов
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
     * Получить SecretKey для подписания (кешируется)
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Генерирует Access Token
     */
    public String generateAccessToken(Long userId, String username, String role) {
        logger.debug("Generating access token for user: {}", username);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("username", username)
                .claim("role", role)
                .claim("tokenType", "ACCESS")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Генерирует Refresh Token
     */
    public String generateRefreshToken(Long userId, String username, String role) {
        logger.debug("Generating refresh token for user: {}", username);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("username", username)
                .claim("role", role)
                .claim("tokenType", "REFRESH")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Валидирует JWT токен
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            logger.debug("Token validation successful");
            return true;

        } catch (Exception ex) {
            logger.warn("Token validation failed: {}", ex.getMessage());
            throw new TokenException("Invalid or expired token");
        }
    }

    /**
     * Получить Claims из токена
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (Exception ex) {
            logger.error("Error extracting claims: {}", ex.getMessage());
            throw new TokenException("Failed to extract claims from token");
        }
    }

    /**
     * Получить username из токена
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Получить userId из токена
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object userId = claims.get("userId");

        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }

        logger.warn("userId is not a number");
        return null;
    }

    /**
     * Получить роль из токена
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (String) claims.get("role");
    }

    /**
     * Получить дату истечения токена
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();

        } catch (Exception ex) {
            logger.error("Error extracting expiration date: {}", ex.getMessage());
            throw new TokenException("Failed to extract expiration date from token");
        }
    }

    /**
     * Получить время жизни токена в миллисекундах
     */
    public long getExpirationTimeMs(String token) {
        try {
            Date expirationDate = getExpirationDateFromToken(token);
            long currentTime = System.currentTimeMillis();
            long expirationTime = expirationDate.getTime() - currentTime;

            if (expirationTime < 0) {
                logger.warn("Token is already expired");
                return 0;
            }

            return expirationTime;

        } catch (Exception ex) {
            logger.error("Error calculating expiration time: {}", ex.getMessage());
            return 0;
        }
    }

    /**
     * Проверить тип токена
     */
    public String getTokenType(String token) {
        Claims claims = getClaimsFromToken(token);
        return (String) claims.get("tokenType");
    }
}
