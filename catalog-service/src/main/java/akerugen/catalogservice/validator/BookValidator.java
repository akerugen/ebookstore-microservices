package akerugen.catalogservice.validator;

import akerugen.catalogservice.dto.request.BookRequestDto;
import akerugen.catalogservice.repo.AuthorRepository;
import akerugen.catalogservice.repo.BookRepository;
import akerugen.catalogservice.repo.BookStatusRepository;
import akerugen.catalogservice.repo.GenreRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookValidator {

    private static final Logger logger = LogManager.getLogger(BookValidator.class);

    private final Validator validator;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookStatusRepository bookStatusRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BookValidator(Validator validator,
                         AuthorRepository authorRepository,
                         GenreRepository genreRepository,
                         BookStatusRepository bookStatusRepository,
                         BookRepository bookRepository) {
        this.validator = validator;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.bookRepository = bookRepository;
    }

    public void validate(BookRequestDto request) {
        logger.debug("Validating BookRequestDto");

        // Валидация аннотаций
        Set<ConstraintViolation<BookRequestDto>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            logger.error("Validation failed: {}", errorMessage);
            throw new IllegalArgumentException("Validation failed: " + errorMessage);
        }

        // Проверка существования Author
        if (!authorRepository.existsById(request.getAuthorId())) {
            logger.error("Author not found with id: {}", request.getAuthorId());
            throw new IllegalArgumentException("Author not found with id: " + request.getAuthorId());
        }

        // Проверка существования Genre
        if (!genreRepository.existsById(request.getGenreId())) {
            logger.error("Genre not found with id: {}", request.getGenreId());
            throw new IllegalArgumentException("Genre not found with id: " + request.getGenreId());
        }

        // Проверка существования BookStatus
        if (!bookStatusRepository.existsById(request.getBookStatusId())) {
            logger.error("BookStatus not found with id: {}", request.getBookStatusId());
            throw new IllegalArgumentException("BookStatus not found with id: " + request.getBookStatusId());
        }

        // Проверка цены
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Price must be greater than zero");
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        logger.debug("BookRequestDto validation passed");
    }
}