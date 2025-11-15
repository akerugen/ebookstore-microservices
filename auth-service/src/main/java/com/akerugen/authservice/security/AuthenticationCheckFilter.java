package com.akerugen.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Специальный фильтр для проверки авторизации на /login и /register
 * Если пользователь АВТОРИЗОВАН (есть JWT в Authorization header):
 * - Блокирует доступ к /login и /register
 */
@Component
@Order(0)
public class AuthenticationCheckFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(AuthenticationCheckFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthenticationCheckFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        logger.debug("AuthenticationCheckFilter: {} {}", method, requestPath);

        boolean isLoginRequest = requestPath.contains("/api/auth/login");
        boolean isRegisterRequest = requestPath.contains("/api/auth/register");
        boolean isPostMethod = "POST".equals(method);

        if (isPostMethod && (isLoginRequest || isRegisterRequest)) {
            logger.debug("Checking authentication for protected endpoint: {} {}", method, requestPath);

            // проверяем есть ли JWT в Authorization header
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    // валидируем токен
                    if (jwtTokenProvider.validateToken(token)) {
                        String username = jwtTokenProvider.getUsernameFromToken(token);

                        String endpointName = isLoginRequest ? "login" : "register";
                        logger.warn("Attempt to {} while authenticated as: {}", endpointName, username);

                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        response.setContentType("application/json;charset=UTF-8");

                        String message = isLoginRequest
                                ? "You are already authenticated. Please logout first to login."
                                : "You are already authenticated. Please logout first to register.";

                        String jsonResponse = String.format(
                                "{\"statusCode\":409,\"message\":\"%s\",\"path\":\"%s\",\"timestamp\":\"%s\"}",
                                message,
                                requestPath,
                                java.time.LocalDateTime.now()
                        );

                        logger.info("Blocking {} request for authenticated user: {}", endpointName, username);
                        response.getWriter().write(jsonResponse);
                        response.getWriter().flush();
                        return;  // останавливаем цепь фильтров
                    }
                } catch (Exception ex) {
                    logger.debug("Token validation error (expected for invalid tokens): {}", ex.getMessage());
                }
            } else {
                logger.debug("No Authorization header found - allowing request");
            }
        }

        // пускаем дальше в цепь
        filterChain.doFilter(request, response);
    }
}
