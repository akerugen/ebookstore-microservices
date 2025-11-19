package com.akerugen.authservice.controller;

import com.akerugen.authservice.dto.request.*;
import com.akerugen.authservice.dto.response.AuthResponse;
import com.akerugen.authservice.dto.response.LogoutResponse;
import com.akerugen.authservice.dto.response.ValidationResponse;
import com.akerugen.authservice.exception.ErrorResponse;
import com.akerugen.authservice.security.JwtTokenProvider;
import com.akerugen.authservice.service.AuthService;
import com.akerugen.authservice.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public AuthController(AuthService authService, AuthenticationUtil authenticationUtil, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.authenticationUtil = authenticationUtil;
        this.jwtTokenProvider = jwtTokenProvider;
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
}
