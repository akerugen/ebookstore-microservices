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
import com.akerugen.authservice.feign.dto.CheckUserExistsResponse;
import com.akerugen.authservice.feign.dto.UserRequestDto;
import com.akerugen.authservice.feign.dto.UserResponseDto;
import com.akerugen.authservice.security.JwtTokenProvider;
import com.akerugen.authservice.service.AuthService;
import com.akerugen.authservice.service.CredentialsService;
import com.akerugen.authservice.service.RefreshTokenService;
import com.akerugen.authservice.service.TokenBlacklistService;
import com.akerugen.authservice.validator.CredentialsValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    private final CredentialsService credentialsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final CredentialsValidator credentialsValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

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

    /**
     * Регистрирует нового пользователя
     * 1. Валидирует данные
     * 2. Проверяет уникальность через user-service
     * 3. Создаёт credentials в auth-service
     * 4. Создаёт профиль в user-service
     * 5. Генерирует токены
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        logger.info("Starting registration for user: {}", request.getUsername());

        // 1. Валидация
        credentialsValidator.validateRegisterRequest(request);

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            logger.warn("Passwords don't match for user: {}", request.getUsername());
            throw new AuthenticationException("Passwords do not match");
        }

        try {
            // 2. Проверяем уникальность через user-service
            CheckUserExistsResponse existsResponse = userServiceClient.checkUserExists(
                    request.getUsername(),
                    request.getEmail()
            );

            if (existsResponse.isExists()) {
                logger.warn("User already exists: {}", request.getUsername());
                throw new AuthenticationException("User with this username or email already exists");
            }

            // 3. Создаём credentials в auth-service
            Credentials credentials = credentialsService.createCredentials(request);
            logger.info("Credentials created for user: {}", request.getUsername());

            // Проверка сессии
            if (refreshTokenService.hasActiveSession(credentials.getUsername())) {
                logger.warn("User already has an active session");
                throw new AuthenticationException(
                        "User already has an active session. Please logout first before registering a new account."
                );
            }

            // 4. Создаём профиль в user-service
            UserRequestDto userRequest = new UserRequestDto(
                    request.getUsername(),
                    request.getEmail(),
                    request.getFirstName(),
                    request.getLastName()
            );

            UserResponseDto userResponse = userServiceClient.createUser(userRequest);
            if (userResponse == null || userResponse.getId() == null) {
                throw new AuthenticationException("Failed to create user profile in user-service");
            }

            logger.info("User profile created in user-service for id: {}", userResponse.getId());

            // 5. Генерируем токены
            String role = credentials.getRole().getAuthority();
            String accessToken = jwtTokenProvider.generateAccessToken(
                    userResponse.getId(),
                    credentials.getUsername(),
                    role
            );
            String refreshToken = jwtTokenProvider.generateRefreshToken(
                    userResponse.getId(),
                    credentials.getUsername(),
                    role
            );

            refreshTokenService.storeRefreshToken(
                    refreshToken,
                    credentials.getUsername(),
                    refreshTokenExpiration
            );

            logger.info("Registration completed for user: {}", request.getUsername());

            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    userResponse.getId(),
                    credentials.getUsername(),
                    accessTokenExpiration
            );

        } catch (Exception ex) {
            logger.error("Registration failed: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Логирует пользователя
     * 1. Ищет credentials по username/email
     * 2. Проверяет активность
     * 3. Проверяет пароль
     * 4. Обновляет lastLogin
     * 5. Генерирует токены
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        logger.info("Starting login for user: {}", request.getUsernameOrEmail());

        try {
            // 1. Ищем credentials
            Credentials credentials = credentialsService.findByUsernameOrEmail(
                    request.getUsernameOrEmail(),
                    request.getUsernameOrEmail()
            );

            // 2. Проверяем сессию активность
            if (refreshTokenService.hasActiveSession(credentials.getUsername())) {
                logger.warn("User already has an active session");
                throw new AuthenticationException(
                        "User already has an active session. Please logout first before logging in again."
                );
            }
            if (!credentials.getIsActive()) {
                logger.warn("User account is deactivated: {}", credentials.getUsername());
                throw new AuthenticationException("User account is deactivated");
            }

            // 3. Проверяем пароль
            if (!passwordEncoder.matches(request.getPassword(), credentials.getPassword())) {
                logger.warn("Invalid password for user: {}", credentials.getUsername());
                credentialsService.incrementFailedLoginAttempts(credentials.getUsername());
                throw new AuthenticationException("Invalid credentials");
            }

            // 4. Сбрасываем счётчик неудачных попыток
            credentialsService.resetFailedLoginAttempts(credentials.getUsername());
            credentialsService.updateLastLogin(credentials.getUsername());

            // 5. Генерируем токены
            // Важно: userId может быть null для SUPER_USER, используем username как основу
            String role = credentials.getRole().getAuthority();
            String accessToken = jwtTokenProvider.generateAccessToken(
                    credentials.getId(),  // используем ID из credentials
                    credentials.getUsername(),
                    role
            );
            String refreshToken = jwtTokenProvider.generateRefreshToken(
                    credentials.getId(),
                    credentials.getUsername(),
                    role
            );

            refreshTokenService.storeRefreshToken(
                    refreshToken,
                    credentials.getUsername(),
                    refreshTokenExpiration
            );

            logger.info("Login successful for user: {} with role: {}", credentials.getUsername(), role);

            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    credentials.getId(),
                    credentials.getUsername(),
                    accessTokenExpiration
            );

        } catch (Exception ex) {
            logger.error("Login failed: {}", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Восстанавливает access token
     */
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        logger.info("Starting token refresh");

        try {
            String refreshToken = request.getRefreshToken();

            jwtTokenProvider.validateToken(refreshToken);
            refreshTokenService.isRefreshTokenValid(refreshToken);

            Long credentialsId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            String role = jwtTokenProvider.getRoleFromToken(refreshToken);

            String newAccessToken = jwtTokenProvider.generateAccessToken(credentialsId, username, role);

            logger.info("Token refreshed for user: {} with role: {}", username, role);

            return new AuthResponse(
                    newAccessToken,
                    refreshToken,
                    "Bearer",
                    credentialsId,
                    username,
                    accessTokenExpiration
            );

        } catch (Exception ex) {
            logger.error("Token refresh failed: {}", ex.getMessage());
            throw new TokenException("Failed to refresh token: " + ex.getMessage());
        }
    }

    /**
     * Валидирует JWT токен
     */
    @Override
    public ValidationResponse validateToken(String token) {
        try {
            // 1.  Проверяем blacklist ПЕРВЫМ!
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                logger.warn("Token validation failed: token is blacklisted");
                return new ValidationResponse(false, null, null, null);
            }

            // 2. Валидируем JWT подпись и TTL
            jwtTokenProvider.validateToken(token);

            // 3. Извлекаем данные из токена
            Long credentialsId = jwtTokenProvider.getUserIdFromToken(token);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);

            logger.debug("Token validated for user: {} with role: {}", username, role);

            return new ValidationResponse(true, credentialsId, username, role);

        } catch (TokenException ex) {
            logger.error("Token validation failed: {}", ex.getMessage());
            return new ValidationResponse(false, null, null, null);
        }
    }

    /**
     * Логирует пользователя из системы
     */
    @Override
    public void logout(String refreshToken, String accessToken) {
        try {
            logger.info("Processing logout");

            String username = refreshTokenService.getUsernameFromToken(refreshToken);

            refreshTokenService.revokeAllUserTokens(refreshToken);

            try {
                // Получаем оставшееся время жизни access token'а
                long accessTokenTTL = jwtTokenProvider.getExpirationTimeMs(accessToken);

                if (accessTokenTTL > 0) {
                    tokenBlacklistService.blacklistToken(accessToken, accessTokenTTL);
                    logger.info("Access token added to blacklist for user: {}, TTL: {}ms", username, accessTokenTTL);
                } else {
                    logger.warn("Access token already expired, skipping blacklist");
                }
            } catch (Exception ex) {
                logger.warn("Failed to blacklist access token: {}", ex.getMessage());
            }

            logger.info("User {} logged out successfully", username);

        } catch (Exception ex) {
            logger.error("Logout failed: {}", ex.getMessage());
            throw new TokenException("Logout failed");
        }
    }

    /**
     * Изменить роль пользователя (только для SUPER_USER)
     */
    @Override
    public void changeUserRole(String username, String newRole) {
        logger.info("Changing role for user: {} to {}", username, newRole);
        credentialsService.changeUserRole(username, newRole);
        logger.info("Role changed successfully for user: {}", username);
    }
}