package akerugen.catalogservice.service.impl;

import akerugen.catalogservice.dto.request.BookRequestDto;
import akerugen.catalogservice.dto.response.BookResponseDto;
import akerugen.catalogservice.entity.Book;
import akerugen.catalogservice.entity.Author;
import akerugen.catalogservice.entity.Genre;
import akerugen.catalogservice.entity.BookStatus;
import akerugen.catalogservice.mapper.BookMapper;
import akerugen.catalogservice.repo.BookRepository;
import akerugen.catalogservice.repo.AuthorRepository;
import akerugen.catalogservice.repo.GenreRepository;
import akerugen.catalogservice.repo.BookStatusRepository;
import akerugen.catalogservice.service.BookService;
import akerugen.catalogservice.validator.BookValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private static final Logger logger = LogManager.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookStatusRepository bookStatusRepository;
    private final BookMapper bookMapper;
    private final BookValidator bookValidator;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository,
                           AuthorRepository authorRepository,
                           GenreRepository genreRepository,
                           BookStatusRepository bookStatusRepository,
                           BookMapper bookMapper,
                           BookValidator bookValidator) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.bookMapper = bookMapper;
        this.bookValidator = bookValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> getAllBooks() {
        logger.info("Fetching all books");
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        logger.info("Fetching book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        return bookMapper.toResponseDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> getBooksByAuthor(Long authorId) {
        logger.info("Fetching books by author id: {}", authorId);
        if (!authorRepository.existsById(authorId)) {
            throw new RuntimeException("Author not found with id: " + authorId);
        }
        List<Book> books = bookRepository.findByAuthorId(authorId);
        return books.stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> getBooksByGenre(Long genreId) {
        logger.info("Fetching books by genre id: {}", genreId);
        if (!genreRepository.existsById(genreId)) {
            throw new RuntimeException("Genre not found with id: " + genreId);
        }
        List<Book> books = bookRepository.findByGenreId(genreId);
        return books.stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> getBooksByStatus(Long statusId) {
        logger.info("Fetching books by status id: {}", statusId);
        if (!bookStatusRepository.existsById(statusId)) {
            throw new RuntimeException("BookStatus not found with id: " + statusId);
        }
        List<Book> books = bookRepository.findByBookStatusId(statusId);
        return books.stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> searchByTitle(String title) {
        logger.info("Searching books by title: {}", title);
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        return books.stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponseDto createBook(BookRequestDto request) {
        logger.info("Creating book with title: {}", request.getTitle());
        bookValidator.validate(request);

        Book book = bookMapper.toEntity(request);
        book.setAuthor(authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found")));
        book.setGenre(genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found")));
        book.setBookStatus(bookStatusRepository.findById(request.getBookStatusId())
                .orElseThrow(() -> new RuntimeException("BookStatus not found")));
        book.setCreatedAt(LocalDateTime.now());

        Book savedBook = bookRepository.save(book);
        logger.info("Book created with id: {}", savedBook.getId());
        return bookMapper.toResponseDto(savedBook);
    }

    @Override
    public BookResponseDto updateBook(Long id, BookRequestDto request) {
        logger.info("Updating book with id: {}", id);
        bookValidator.validate(request);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        book.setTitle(request.getTitle());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());
        book.setAuthor(authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found")));
        book.setGenre(genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found")));
        book.setBookStatus(bookStatusRepository.findById(request.getBookStatusId())
                .orElseThrow(() -> new RuntimeException("BookStatus not found")));
        book.setUpdatedAt(LocalDateTime.now());

        Book updatedBook = bookRepository.save(book);
        logger.info("Book updated with id: {}", id);
        return bookMapper.toResponseDto(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        logger.info("Deleting book with id: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
        logger.info("Book deleted with id: {}", id);
    }

    @Override
    public void deleteAllBooks() {
        logger.info("Deleting all books");
        bookRepository.deleteAll();
    }
}
