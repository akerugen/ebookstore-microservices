package akerugen.catalogservice.validator;

import akerugen.catalogservice.dto.request.GenreRequestDto;
import akerugen.catalogservice.repo.GenreRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GenreValidator {

    private static final Logger logger = LogManager.getLogger(GenreValidator.class);

    private final Validator validator;
    private final GenreRepository genreRepository;

    @Autowired
    public GenreValidator(Validator validator, GenreRepository genreRepository) {
        this.validator = validator;
        this.genreRepository = genreRepository;
    }

    public void validate(GenreRequestDto request) {
        logger.debug("Validating GenreRequestDto");

        Set<ConstraintViolation<GenreRequestDto>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            logger.error("Validation failed: {}", errorMessage);
            throw new IllegalArgumentException("Validation failed: " + errorMessage);
        }

        // Проверка на уникальность имени жанра (если создание)
        if (genreRepository.existsByName(request.getName())) {
            logger.error("Genre already exists with name: {}", request.getName());
            throw new IllegalArgumentException("Genre already exists with name: " + request.getName());
        }

        logger.debug("GenreRequestDto validation passed");
    }
}