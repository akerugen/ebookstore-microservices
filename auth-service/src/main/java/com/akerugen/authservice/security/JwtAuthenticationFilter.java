package com.akerugen.authservice.security;

import com.akerugen.authservice.exception.TokenException;
import com.akerugen.authservice.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter
 * Извлекает JWT токен из Authorization header
 * Валидирует токен
 * Помещает аутентифицированного пользователя в SecurityContext
 */
@Component
@Order(1)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, TokenBlacklistService tokenBlacklistService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null && !jwt.isEmpty()) {
                logger.debug("JWT token found in request");

                if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                    logger.warn("Token is blacklisted - rejecting");
                    // Не аутентифицируем, продолжаем цепь
                    filterChain.doFilter(request, response);
                    return;
                }

                // валидируем токен
                if (jwtTokenProvider.validateToken(jwt)) {
                    // извлекаем данные из токена
                    Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                    String username = jwtTokenProvider.getUsernameFromToken(jwt);
                    String role = jwtTokenProvider.getRoleFromToken(jwt);

                    logger.debug("Token validated for user: {} with role: {}", username, role);

                    // создаём аутентификацию
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    Collections.singletonList(authority)
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // помещаем в SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("User {} authenticated in SecurityContext", username);
                } else {
                    logger.warn("JWT token validation failed");
                }
            } else {
                logger.debug("No JWT token found in request");
            }
        } catch (TokenException ex) {
            logger.error("JWT token error: {}", ex.getMessage());
            // не бросаем исключение, просто не аутентифицируем
        } catch (Exception ex) {
            logger.error("Error processing JWT token: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT из Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
