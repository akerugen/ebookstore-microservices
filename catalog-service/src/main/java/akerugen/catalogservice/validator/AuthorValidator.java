package akerugen.catalogservice.validator;

import akerugen.catalogservice.dto.request.AuthorRequestDto;
import akerugen.catalogservice.repo.AuthorRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthorValidator {

    private static final Logger logger = LogManager.getLogger(AuthorValidator.class);

    private final Validator validator;
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorValidator(Validator validator, AuthorRepository authorRepository) {
        this.validator = validator;
        this.authorRepository = authorRepository;
    }

    public void validate(AuthorRequestDto request) {
        logger.debug("Validating AuthorRequestDto");

        Set<ConstraintViolation<AuthorRequestDto>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            logger.error("Validation failed: {}", errorMessage);
            throw new IllegalArgumentException("Validation failed: " + errorMessage);
        }

        logger.debug("AuthorRequestDto validation passed");
    }
}
