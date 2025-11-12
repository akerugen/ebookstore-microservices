package com.akerugen.authservice.service.impl;

import com.akerugen.authservice.dto.request.LoginRequest;
import com.akerugen.authservice.dto.request.RegisterRequest;
import com.akerugen.authservice.dto.request.RefreshTokenRequest;
import com.akerugen.authservice.dto.response.AuthResponse;
import com.akerugen.authservice.dto.response.ValidationResponse;
import com.akerugen.authservice.entity.Credentials;
import com.akerugen.authservice.exception.AuthenticationException;
import com.akerugen.authservice.exception.TokenException;
import com.akerugen.authservice.feign.UserServiceClient;
import com.akerugen.authservice.feign.dto.UserRequestDto;
import com.akerugen.authservice.security.JwtTokenProvider;
import com.akerugen.authservice.service.AuthService;
import com.akerugen.authservice.service.CredentialsService;
import com.akerugen.authservice.service.RefreshTokenService;
import com.akerugen.authservice.validator.CredentialsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final CredentialsService credentialsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final CredentialsValidator credentialsValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public AuthServiceImpl(CredentialsService credentialsService,
                           JwtTokenProvider jwtTokenProvider,
                           RefreshTokenService refreshTokenService,
                           CredentialsValidator credentialsValidator,
                           PasswordEncoder passwordEncoder,
                           UserServiceClient userServiceClient) {
        this.credentialsService = credentialsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.credentialsValidator = credentialsValidator;
        this.passwordEncoder = passwordEncoder;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        logger.info("Starting registration for user: {}", request.getUsername());

        // валидация данных
        credentialsValidator.validateRegisterRequest(request);

        try {
            // 1. Создаем пользователя в user-service
            UserRequestDto userRequest = new UserRequestDto(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword()
            );

            var userResponse = userServiceClient.createUser(userRequest).getBody();
            if (userResponse == null || userResponse.id == null) {
                throw new AuthenticationException("Failed to create user in user-service");
            }

            Long userId = userResponse.id;
            logger.info("User created in user-service with id: {}", userId);

            // 2. Создаем credentials в auth-service
            Credentials credentials = credentialsService.createCredentials(request, userId);
            logger.info("Credentials created for user: {}", userId);

            // 3. Генерируем токены
            String accessToken = jwtTokenProvider.generateAccessToken(userId, request.getUsername());
            String refreshToken = jwtTokenProvider.generateRefreshToken(userId, request.getUsername());

            // 4. Сохраняем refresh token
            refreshTokenService.storeRefreshToken(refreshToken, userId, refreshTokenExpiration);

            logger.info("Registration completed for user: {}", request.getUsername());

            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    userId,
                    request.getUsername(),
                    accessTokenExpiration
            );

        } catch (Exception ex) {
            logger.error("Registration failed: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        logger.info("Starting login for user: {}", request.getUsernameOrEmail());

        try {
            // ищем юзера по username или email
            Credentials credentials = credentialsService.findByUsernameOrEmail(
                    request.getUsernameOrEmail(),
                    request.getUsernameOrEmail()
            );

            // проверяем статус
            if (!credentials.getIsActive()) {
                throw new AuthenticationException("User account is deactivated");
            }

            // проверяем пароль
            if (!passwordEncoder.matches(request.getPassword(), credentials.getPassword())) {
                logger.warn("Invalid password for user: {}", credentials.getUsername());
                throw new AuthenticationException("Invalid credentials");
            }

            Long userId = credentials.getUserId();
            String username = credentials.getUsername();

            // генерим токены
            String accessToken = jwtTokenProvider.generateAccessToken(userId, username);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userId, username);

            // сохраняем refresh token
            refreshTokenService.storeRefreshToken(refreshToken, userId, refreshTokenExpiration);

            logger.info("Login successful for user: {}", username);

            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    userId,
                    username,
                    accessTokenExpiration
            );

        } catch (Exception ex) {
            logger.error("Login failed: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        logger.info("Starting token refresh");

        try {
            // валидируем refresh token
            String refreshToken = request.getRefreshToken();
            jwtTokenProvider.validateToken(refreshToken);
            refreshTokenService.isRefreshTokenValid(refreshToken);

            // извлекаем userId из refresh token
            Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

            // генерим новый access token
            String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username);

            logger.info("Token refreshed for user: {}", userId);

            return new AuthResponse(
                    newAccessToken,
                    refreshToken,
                    "Bearer",
                    userId,
                    username,
                    accessTokenExpiration
            );

        } catch (Exception ex) {
            logger.error("Token refresh failed: {}", ex.getMessage());
            throw new TokenException("Failed to refresh token: " + ex.getMessage());
        }
    }

    @Override
    public ValidationResponse validateToken(String token) {
        try {
            jwtTokenProvider.validateToken(token);
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            String username = jwtTokenProvider.getUsernameFromToken(token);

            logger.debug("Token validated for user: {}", userId);

            return new ValidationResponse(true, userId, username);

        } catch (TokenException ex) {
            logger.error("Token validation failed: {}", ex.getMessage());
            return new ValidationResponse(false, null, null);
        }
    }

    @Override
    public void logout(String refreshToken) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            refreshTokenService.revokeAllUserTokens(userId);
            logger.info("User logged out: {}", userId);
        } catch (Exception ex) {
            logger.error("Logout failed: {}", ex.getMessage());
            throw new TokenException("Logout failed");
        }
    }
}
