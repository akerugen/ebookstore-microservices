package akerugen.catalogservice.validator;

import akerugen.catalogservice.dto.request.BookStatusRequestDto;
import akerugen.catalogservice.repo.BookStatusRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookStatusValidator {

    private static final Logger logger = LogManager.getLogger(BookStatusValidator.class);

    private final Validator validator;
    private final BookStatusRepository bookStatusRepository;

    @Autowired
    public BookStatusValidator(Validator validator, BookStatusRepository bookStatusRepository) {
        this.validator = validator;
        this.bookStatusRepository = bookStatusRepository;
    }

    public void validate(BookStatusRequestDto request) {
        logger.debug("Validating BookStatusRequestDto");

        Set<ConstraintViolation<BookStatusRequestDto>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            logger.error("Validation failed: {}", errorMessage);
            throw new IllegalArgumentException("Validation failed: " + errorMessage);
        }

        // Проверка на уникальность имени статуса
        if (bookStatusRepository.existsByName(request.getName())) {
            logger.error("BookStatus already exists with name: {}", request.getName());
            throw new IllegalArgumentException("BookStatus already exists with name: " + request.getName());
        }

        logger.debug("BookStatusRequestDto validation passed");
    }
}