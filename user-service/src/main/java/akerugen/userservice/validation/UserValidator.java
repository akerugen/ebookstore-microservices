package akerugen.userservice.validation;

import akerugen.userservice.dto.request.UserRequestDto;
import akerugen.userservice.repo.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserValidator {
    private final Validator validator;
    private final UserRepository userRepository;

    @Autowired
    public UserValidator(Validator validator, UserRepository userRepository) {
        this.validator = validator;
        this.userRepository = userRepository;
    }

    public void validate(UserRequestDto request, Long userId, boolean isPartialUpdate) {

        validateAnnotations(request, isPartialUpdate);

        if (request.getUsername() != null) {
            validateUsername(request.getUsername(), userId);
        }
        if (request.getEmail() != null) {
            validateEmail(request.getEmail(), userId);
        }
        if (request.getPassword() != null) {
            validatePassword(request.getPassword());
        }
    }

    // Проверка аннотаций
    private void validateAnnotations(UserRequestDto request, boolean isPartialUpdate) {
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(request);
        if (!isPartialUpdate && !violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Validation failed: " + errorMessage);
        }
    }

    // Проверка формата username
    private void validateUsername(String username, Long userId) {
        if (!username.matches("^[a-zA-Z0-9_]{3,50}$")) {
            throw new IllegalArgumentException("Username must contain only letters, numbers" +
                    " and be 3-50 characters long");
        }

        userRepository.findByUsername(username)
                .filter(user -> userId == null || !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username already exists: " + username);
                });
    }

    // Проверка формата почты
    private void validateEmail(String email, Long userId) {
        if (email.trim().length() != email.length()) {
            throw new IllegalArgumentException("Email must not contain leading or trailing spaces");
        }

        userRepository.findByEmail(email)
                .filter(user -> userId == null || !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email already exists: " + email);
                });
    }

    private void validatePassword(String password) {
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
            throw new IllegalArgumentException("Password must be at least 6 characters and contain at least " +
                    "one uppercase letter, one lowercase letter, and one digit");
        }
    }
}

