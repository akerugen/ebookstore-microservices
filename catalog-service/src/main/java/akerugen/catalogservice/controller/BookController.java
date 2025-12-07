package akerugen.catalogservice.controller;

import akerugen.catalogservice.dto.request.BookRequestDto;
import akerugen.catalogservice.dto.response.BookResponseDto;
import akerugen.catalogservice.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/books")
@Tag(name = "Books Management", description = "Operations related to book management")
public class BookController {

    private static final Logger logger = LogManager.getLogger(BookController.class);
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieves all books from catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    })
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        logger.info("GET /api/catalog/books - retrieving all books");
        List<BookResponseDto> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieves a book by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        logger.info("GET /api/catalog/books/{} - retrieving book", id);
        BookResponseDto book = bookService.getBookById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @GetMapping("/search/title")
    @Operation(summary = "Search books by title", description = "Searches books by title (case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned")
    })
    public ResponseEntity<List<BookResponseDto>> searchByTitle(@RequestParam String title) {
        logger.info("GET /api/catalog/books/search/title - searching by title: {}", title);
        List<BookResponseDto> books = bookService.searchByTitle(title);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/filter/author/{authorId}")
    @Operation(summary = "Get books by author", description = "Retrieves all books by a specific author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<List<BookResponseDto>> getBooksByAuthor(@PathVariable Long authorId) {
        logger.info("GET /api/catalog/books/filter/author/{} - retrieving books by author", authorId);
        List<BookResponseDto> books = bookService.getBooksByAuthor(authorId);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/filter/genre/{genreId}")
    @Operation(summary = "Get books by genre", description = "Retrieves all books of a specific genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public ResponseEntity<List<BookResponseDto>> getBooksByGenre(@PathVariable Long genreId) {
        logger.info("GET /api/catalog/books/filter/genre/{} - retrieving books by genre", genreId);
        List<BookResponseDto> books = bookService.getBooksByGenre(genreId);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/filter/status/{statusId}")
    @Operation(summary = "Get books by status", description = "Retrieves all books with a specific status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Status not found")
    })
    public ResponseEntity<List<BookResponseDto>> getBooksByStatus(@PathVariable Long statusId) {
        logger.info("GET /api/catalog/books/filter/status/{} - retrieving books by status", statusId);
        List<BookResponseDto> books = bookService.getBooksByStatus(statusId);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new book", description = "Creates a new book in the catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<BookResponseDto> createBook(@Valid @RequestBody BookRequestDto request) {
        logger.info("POST /api/catalog/books - creating book with title: {}", request.getTitle());
        BookResponseDto book = bookService.createBook(request);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update book", description = "Updates an existing book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable Long id,
                                                      @Valid @RequestBody BookRequestDto request) {
        logger.info("PUT /api/catalog/books/{} - updating book", id);
        BookResponseDto book = bookService.updateBook(id, request);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Deletes a book from the catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        logger.info("DELETE /api/catalog/books/{} - deleting book", id);
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Operation(summary = "Delete all books", description = "Deletes all books from the catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All books deleted successfully")
    })
    public ResponseEntity<Void> deleteAllBooks() {
        logger.info("DELETE /api/catalog/books - deleting all books");
        bookService.deleteAllBooks();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}