package com.akerugen.authservice.controller;

import com.akerugen.authservice.dto.request.LoginRequest;
import com.akerugen.authservice.dto.request.RegisterRequest;
import com.akerugen.authservice.dto.request.RefreshTokenRequest;
import com.akerugen.authservice.dto.response.AuthResponse;
import com.akerugen.authservice.dto.response.ValidationResponse;
import com.akerugen.authservice.exception.ErrorResponse;
import com.akerugen.authservice.service.AuthService;
import com.akerugen.authservice.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    public AuthController(AuthService authService, AuthenticationUtil authenticationUtil) {
        this.authService = authService;
        this.authenticationUtil = authenticationUtil;
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

        // Проверяем, авторизован ли пользователь
        if (authenticationUtil.isUserAuthenticated()) {
            String currentUsername = authenticationUtil.getCurrentUsername();
            logger.warn("Attempt to register while authenticated as: {}", currentUsername);

            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "You are already authenticated as '" + currentUsername + "'. Please logout first to register a new account.",
                    "/api/auth/register",
                    LocalDateTime.now()
            ));
        }

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

        // Проверяем, авторизован ли пользователь
        if (authenticationUtil.isUserAuthenticated()) {
            String currentUsername = authenticationUtil.getCurrentUsername();
            logger.warn("Attempt to login while authenticated as: {}", currentUsername);

            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "You are already authenticated as '" + currentUsername + "'. Please logout first to login as another user.",
                    "/api/auth/login",
                    LocalDateTime.now()
            ));
        }

        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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

    @PostMapping("/validate")
    @Operation(summary = "Validate access token", description = "Validates JWT access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result returned"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ValidationResponse> validateToken(
            @RequestParam String token) {
        logger.debug("Received token validation request");
        ValidationResponse response = authService.validateToken(token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Revokes all refresh tokens for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> logout(
            @RequestParam String refreshToken) {
        logger.info("Received logout request");
        authService.logout(refreshToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>("Auth service is running", HttpStatus.OK);
    }
}
