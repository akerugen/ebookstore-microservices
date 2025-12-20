package com.akerugen.authservice.controller;

import com.akerugen.authservice.dto.request.*;
import com.akerugen.authservice.dto.response.AuthResponse;
import com.akerugen.authservice.dto.response.LogoutResponse;
import com.akerugen.authservice.dto.response.UserRoleResponse;
import com.akerugen.authservice.dto.response.ValidationResponse;
import com.akerugen.authservice.exception.ErrorResponse;
import com.akerugen.authservice.security.JwtTokenProvider;
import com.akerugen.authservice.service.AuthService;
import com.akerugen.authservice.service.CredentialsService;
import com.akerugen.authservice.util.AuthenticationUtil;
import com.akerugen.authservice.util.RoleAuthorizationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final AuthenticationUtil authenticationUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleAuthorizationUtil roleAuthorizationUtil;
    private final CredentialsService credentialsService;

    public AuthController(AuthService authService, AuthenticationUtil authenticationUtil, JwtTokenProvider jwtTokenProvider, RoleAuthorizationUtil roleAuthorizationUtil, CredentialsService credentialsService) {
        this.authService = authService;
        this.authenticationUtil = authenticationUtil;
        this.jwtTokenProvider = jwtTokenProvider;
        this.roleAuthorizationUtil = roleAuthorizationUtil;
        this.credentialsService = credentialsService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Register endpoint called");

        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "409", description = "User already authenticated - please logout first"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login endpoint called for user: {}", request.getUsernameOrEmail());

        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Логирует пользователя из системы
     * Refresh token передаётся в теле запроса (POST body), а НЕ в URL
     * Это безопаснее, так как:
     * - Не видно в браузер истории
     * - Не логируется в URL логах
     * - Не передаётся через Referer header
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Revokes user's tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing access token in header"),
            @ApiResponse(responseCode = "400", description = "Missing tokens in body"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LogoutResponse> logout(
            HttpServletRequest httpRequest,
            @Valid @RequestBody LogoutRequest request) {

        logger.info("Logout endpoint called");

        if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            throw new IllegalArgumentException("refreshToken is required");
        }

        try {
            // Извлекаем access token из Authorization header
            String authHeader = httpRequest.getHeader("Authorization");
            String accessToken = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            } else {
                throw new org.springframework.security.authentication.BadCredentialsException(
                        "Authorization header with Bearer token is required"
                );
            }

            String username = jwtTokenProvider.getUsernameFromToken(request.getRefreshToken());

            // Передаём оба токена
            authService.logout(request.getRefreshToken(), accessToken);

            LogoutResponse response = new LogoutResponse(
                    200,
                    "User logged out successfully",
                    username
            );

            logger.info("User {} logged out successfully", username);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception ex) {
            logger.error("Logout failed: {}", ex.getMessage());
            throw ex;
        }
    }


    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates a new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        logger.info("Received token refresh request");
        AuthResponse response = authService.refreshToken(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Валидирует access token
     * Authorization Header требуется для аутентификации
     * Token в body для валидации
     */
    @PostMapping("/validate")
    @Operation(summary = "Validate access token", description = "Validates JWT access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid access token"),
            @ApiResponse(responseCode = "400", description = "Missing token in body"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ValidationResponse> validateToken(
            @Valid @RequestBody ValidateTokenRequest request) {

        logger.debug("Received token validation request");

        if (request.getToken() == null || request.getToken().isEmpty()) {
            throw new IllegalArgumentException("token is required");
        }

        ValidationResponse response = authService.validateToken(request.getToken());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Валидация токена для Nginx (через header)
     * Используется только Nginx Gateway через auth_request
     * Endpoint: GET /api/auth/validate-header
     */
    @GetMapping("/validate-header")
    @Operation(summary = "Validate token from header (for Nginx)",
            description = "Validates JWT token from Authorization header. Returns 200 if valid, 401 if invalid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Token is invalid or missing")
    })
    public ResponseEntity<Void> validateTokenFromHeader(HttpServletRequest request) {
        logger.debug("Received token validation request from Nginx");

        // для preflight CORS-запросов (OPTIONS) токен не передаётся, но нам нужно
        // просто подтвердить nginx, что маршрут доступен. поэтому сразу возвращаем 200.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.debug("CORS preflight (OPTIONS) request - skipping token validation");
            return ResponseEntity.ok().build();
        }

        try {
            // Извлекаем токен из Authorization header
            String authHeader = request.getHeader("Authorization");

            logger.warn("Authorization header value: {}",
                    authHeader != null ? (authHeader.length() > 20 ? authHeader.substring(0, 20) + "..." : authHeader) : "NULL");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("No Authorization header or invalid format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);
            logger.debug("Token extracted, length: {}", token.length());

            // Валидируем токен
            ValidationResponse validation = authService.validateToken(token);

            if (Boolean.TRUE.equals(validation.getValid())) {
                logger.debug("Token is valid for user: {}", validation.getUsername());

                String role = jwtTokenProvider.getRoleFromToken(token);
                logger.debug("User role: {}", role);
                logger.info("Token valid, returning role: {}", role);

                // возвращаем 200 OK - nginx пропустит запрос
                return ResponseEntity.ok()
                        .header("X-User-Roles", role)
                        .build();
            } else {
                logger.warn("Token validation failed");
                // возвращаем 401 Unauthorized - nginx отклонит запрос
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        } catch (Exception ex) {
            logger.error("Token validation error: {}", ex.getMessage());
            logger.error("Exception class: {}", ex.getClass().getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Изменить роль пользователя (только для SUPER_USER)
     */
    @PatchMapping("/change-role")
    @Operation(summary = "Change user role", description = "Changes user role. Only SUPER_USER can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - only SUPER_USER can change roles"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> changeUserRole(
            HttpServletRequest httpRequest,
            @Valid @RequestBody ChangeRoleRequest request) {

        logger.info("Change role endpoint called for user: {} to role: {}", request.getUsername(), request.getNewRole());

        // Проверяем заголовок Authorization
        String authHeader = httpRequest.getHeader("Authorization");
        logger.warn("Authorization header in changeUserRole: {}", 
                authHeader != null ? (authHeader.length() > 20 ? authHeader.substring(0, 20) + "..." : authHeader) : "NULL");

        // Проверяем, что текущий пользователь - SUPER_USER
        if (!roleAuthorizationUtil.isSuperUser(httpRequest)) {
            logger.warn("User does not have SUPER_USER role to change user roles. Auth header present: {}", 
                    authHeader != null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(403, "Forbidden: Only SUPER_USER can change user roles", 
                            httpRequest.getRequestURI(), LocalDateTime.now()));
        }

        try {
            authService.changeUserRole(request.getUsername(), request.getNewRole());
            return ResponseEntity.ok().body(new ErrorResponse(200, "Role changed successfully", 
                    httpRequest.getRequestURI(), LocalDateTime.now()));
        } catch (Exception ex) {
            logger.error("Failed to change user role: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, ex.getMessage(), 
                            httpRequest.getRequestURI(), LocalDateTime.now()));
        }
    }

    /**
     * Получить роль пользователя по username
     */
    @GetMapping("/user-role/{username}")
    @Operation(summary = "Get user role by username", description = "Retrieves user role by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token")
    })
    public ResponseEntity<UserRoleResponse> getUserRole(@PathVariable String username) {
        logger.info("GET /api/auth/user-role/{} - retrieving user role", username);
        try {
            var credentials = credentialsService.findByUsername(username);
            String role = credentials.getRole().name(); // USER, ADMIN, SUPER_USER
            UserRoleResponse response = new UserRoleResponse(username, role);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Failed to get user role: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
