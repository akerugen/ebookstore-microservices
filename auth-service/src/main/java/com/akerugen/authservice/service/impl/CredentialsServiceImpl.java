package com.akerugen.authservice.service.impl;

import com.akerugen.authservice.dto.request.RegisterRequest;
import com.akerugen.authservice.entity.Credentials;
import com.akerugen.authservice.enums.Role;
import com.akerugen.authservice.exception.AuthenticationException;
import com.akerugen.authservice.repo.CredentialsRepository;
import com.akerugen.authservice.service.CredentialsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CredentialsServiceImpl implements CredentialsService {

    private static final Logger logger = LogManager.getLogger(CredentialsServiceImpl.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CredentialsServiceImpl(CredentialsRepository credentialsRepository,
                                  PasswordEncoder passwordEncoder) {
        this.credentialsRepository = credentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Создать новые credentials при регистрации
     */
    @Override
    public Credentials createCredentials(RegisterRequest request) {
        logger.info("Creating credentials for user: {}", request.getUsername());

        // Хешируем пароль
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Создаём credentials с ролью USER по умолчанию
        Credentials credentials = new Credentials(
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                LocalDateTime.now()
        );

        credentialsRepository.save(credentials);
        logger.info("Credentials created for user: {} with role: {}", request.getUsername(), credentials.getRole());

        return credentials;
    }

    /**
     * Найти credentials по username или email
     */
    @Override
    @Transactional(readOnly = true)
    public Credentials findByUsernameOrEmail(String username, String email) {
        logger.debug("Finding credentials by username or email: {} / {}", username, email);
        return credentialsRepository.findByUsernameOrEmail(username, email)
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }

    @Override
    public Credentials findByUserId(Long userId) {
        return null;
    }

    /**
     * Найти credentials по username
     */
    @Override
    @Transactional(readOnly = true)
    public Credentials findByUsername(String username) {
        logger.debug("Finding credentials by username: {}", username);
        return credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }

    /**
     * Найти credentials по email
     */
    @Override
    @Transactional(readOnly = true)
    public Credentials findByEmail(String email) {
        logger.debug("Finding credentials by email: {}", email);
        return credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }

    /**
     * Проверить существует ли пользователь
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsernameOrEmail(String username, String email) {
        logger.debug("Checking if user exists: username={}, email={}", username, email);
        return credentialsRepository.existsByUsernameOrEmail(username, email);
    }

    @Override
    public void deactivateCredentials(Long userId) {

    }

    /**
     * Деактивировать credentials (удаление аккаунта)
     */
    @Override
    public void deactivateCredentials(String username) {
        logger.info("Deactivating credentials for user: {}", username);

        Credentials credentials = findByUsername(username);
        credentials.setIsActive(false);
        credentials.setUpdatedAt(LocalDateTime.now());
        credentialsRepository.save(credentials);

        logger.info("Credentials deactivated for user: {}", username);
    }

    /**
     * Изменить роль пользователя (может делать только SUPER_USER)
     */
    @Override
    public void changeUserRole(String username, String newRole) {
        logger.info("Changing role for user: {} to {}", username, newRole);

        Credentials credentials = findByUsername(username);

        // Проверка - не пытаемся ли изменить SUPER_USER
        if (credentials.getRole() == Role.SUPER_USER && !newRole.equals("SUPER_USER")) {
            throw new AuthenticationException("Cannot change SUPER_USER role");
        }

        credentials.setRole(Role.fromString(newRole));
        credentials.setUpdatedAt(LocalDateTime.now());
        credentialsRepository.save(credentials);

        logger.info("Role changed for user: {} to {}", username, newRole);
    }

    /**
     * Увеличить счётчик неудачных попыток входа
     * Используется для блокировки после N попыток
     */
    @Override
    public void incrementFailedLoginAttempts(String username) {
        logger.debug("Incrementing failed login attempts for user: {}", username);

        Credentials credentials = findByUsername(username);
        int currentAttempts = credentials.getFailedLoginAttempts() != null ?
                credentials.getFailedLoginAttempts() : 0;

        credentials.setFailedLoginAttempts(currentAttempts + 1);

        // Если превышено максимум попыток - деактивируем
        if (credentials.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            logger.warn("User {} exceeded max failed login attempts, deactivating account", username);
            credentials.setIsActive(false);
        }

        credentialsRepository.save(credentials);
    }

    /**
     * Сбросить счётчик неудачных попыток (успешный логин)
     */
    @Override
    public void resetFailedLoginAttempts(String username) {
        logger.debug("Resetting failed login attempts for user: {}", username);

        Credentials credentials = findByUsername(username);
        credentials.setFailedLoginAttempts(0);
        credentialsRepository.save(credentials);
    }

    /**
     * Обновить время последнего входа
     */
    @Override
    public void updateLastLogin(String username) {
        logger.debug("Updating last login for user: {}", username);

        Credentials credentials = findByUsername(username);
        credentials.setLastLogin(LocalDateTime.now());
        credentialsRepository.save(credentials);
    }
}