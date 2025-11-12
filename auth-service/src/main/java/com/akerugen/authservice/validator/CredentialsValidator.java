package com.akerugen.authservice.validator;

import com.akerugen.authservice.dto.request.RegisterRequest;
import com.akerugen.authservice.exception.AuthenticationException;
import com.akerugen.authservice.repo.CredentialsRepository;
import org.springframework.stereotype.Component;

@Component
public class CredentialsValidator {

    private final CredentialsRepository credentialsRepository;

    public CredentialsValidator(CredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }

    public void validateRegisterRequest(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new AuthenticationException("Username cannot be empty");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new AuthenticationException("Email cannot be empty");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new AuthenticationException("Password cannot be empty");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AuthenticationException("Passwords do not match");
        }

        if (credentialsRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("Username already exists");
        }

        if (credentialsRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("Email already exists");
        }

        // TODO: можно добавить проверку на сложность пароля
        validatePasswordStrength(request.getPassword());
    }

    private void validatePasswordStrength(String password) {

        // минимум: 8 символов, хотя бы одна заглавная, одна цифра, один спецсимвол
        if (password.length() < 8) {
            throw new AuthenticationException("Password must be at least 8 characters long");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new AuthenticationException("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new AuthenticationException("Password must contain at least one digit");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new AuthenticationException("Password must contain at least one special character");
        }
    }
}
