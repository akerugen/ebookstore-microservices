package akerugen.catalogservice.controller;

import akerugen.catalogservice.dto.request.BookRequestDto;
import akerugen.catalogservice.dto.response.BookResponseDto;
import akerugen.catalogservice.feign.NotificationServiceClient;
import akerugen.catalogservice.feign.UserServiceClient;
import akerugen.catalogservice.feign.dto.CreateNotificationRequestDto;
import akerugen.catalogservice.feign.dto.UserResponseDto;
import akerugen.catalogservice.service.BookService;
import akerugen.catalogservice.util.RoleAuthorizationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/catalog/books")
@Tag(name = "Books Management", description = "Operations related to book management")
public class BookController {

    private static final Logger logger = LogManager.getLogger(BookController.class);
    private final BookService bookService;
    private final RoleAuthorizationUtil roleAuthorizationUtil;
    private final NotificationServiceClient notificationServiceClient;
    private final UserServiceClient userServiceClient;

    public BookController(BookService bookService, 
                         RoleAuthorizationUtil roleAuthorizationUtil,
                         NotificationServiceClient notificationServiceClient,
                         UserServiceClient userServiceClient) {
        this.bookService = bookService;
        this.roleAuthorizationUtil = roleAuthorizationUtil;
        this.notificationServiceClient = notificationServiceClient;
        this.userServiceClient = userServiceClient;
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
    @Operation(summary = "Create a new book", description = "Creates a new book in the catalog (requires ADMIN or SUPER_USER role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<BookResponseDto> createBook(
            @Valid @RequestBody BookRequestDto request,
            HttpServletRequest httpRequest) {

        logger.info("POST /api/catalog/books - creating book with title: {}", request.getTitle());

        if (!roleAuthorizationUtil.isAdminOrSuperUser(httpRequest)) {
            logger.warn("User does not have ADMIN or SUPER_USER role to create book");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        BookResponseDto book = bookService.createBook(request);
        
        // создаем уведомление о новой книге для всех пользователей
        Long currentUserId = roleAuthorizationUtil.getUserId(httpRequest);
        createNotificationForAllUsers(book, "BOOK_CREATED", currentUserId);
        
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update book", description = "Updates an existing book (requires ADMIN or SUPER_USER role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<BookResponseDto> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDto request,
            HttpServletRequest httpRequest) {

        logger.info("PUT /api/catalog/books/{} - updating book", id);

        if (!roleAuthorizationUtil.isAdminOrSuperUser(httpRequest)) {
            logger.warn("User does not have ADMIN or SUPER_USER role to update book");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        BookResponseDto oldBook = bookService.getBookById(id);
        BookResponseDto book = bookService.updateBook(id, request);
        
        // создаем уведомление об изменении книги
        Long currentUserId = roleAuthorizationUtil.getUserId(httpRequest);
        // проверяем, изменилась ли цена
        if (oldBook.getPrice() != null && book.getPrice() != null && 
            oldBook.getPrice().compareTo(book.getPrice()) != 0) {
            createNotificationForAllUsers(book, "PRICE_CHANGED", currentUserId, oldBook.getPrice(), book.getPrice());
        } else {
            createNotificationForAllUsers(book, "BOOK_UPDATED", currentUserId);
        }
        
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Deletes a book from the catalog (requires ADMIN or SUPER_USER role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        logger.info("DELETE /api/catalog/books/{} - deleting book", id);

        if (!roleAuthorizationUtil.isAdminOrSuperUser(httpRequest)) {
            logger.warn("User does not have ADMIN or SUPER_USER role to delete book");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // получаем информацию о книге перед удалением для уведомления
        BookResponseDto book = bookService.getBookById(id);
        
        bookService.deleteBook(id);
        
        // создаем уведомление об удалении книги
        Long currentUserId = roleAuthorizationUtil.getUserId(httpRequest);
        createNotificationForAllUsers(book, "BOOK_DELETED", currentUserId);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Operation(summary = "Delete all books", description = "Deletes all books from the catalog (requires ADMIN or SUPER_USER role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All books deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<Void> deleteAllBooks(HttpServletRequest httpRequest) {
        logger.info("DELETE /api/catalog/books - deleting all books");

        if (!roleAuthorizationUtil.isAdminOrSuperUser(httpRequest)) {
            logger.warn("User does not have ADMIN or SUPER_USER role to delete all books");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        bookService.deleteAllBooks();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Создает уведомления для всех пользователей (кроме текущего администратора)
     */
    private void createNotificationForAllUsers(BookResponseDto book, String notificationType, Long excludeUserId) {
        createNotificationForAllUsers(book, notificationType, excludeUserId, null, null);
    }

    /**
     * Создает уведомления для всех пользователей (кроме текущего администратора)
     * @param book книга
     * @param notificationType тип уведомления (BOOK_CREATED, BOOK_UPDATED, BOOK_DELETED, PRICE_CHANGED)
     * @param excludeUserId userId пользователя, для которого не нужно создавать уведомление (обычно администратор)
     * @param oldPrice старая цена (для PRICE_CHANGED)
     * @param newPrice новая цена (для PRICE_CHANGED)
     */
    private void createNotificationForAllUsers(BookResponseDto book, String notificationType, 
                                               Long excludeUserId, BigDecimal oldPrice, 
                                               BigDecimal newPrice) {
        try {
            // получаем список всех пользователей
            List<UserResponseDto> users = userServiceClient.getAllUsers();
            
            if (users == null || users.isEmpty()) {
                logger.debug("No users found, skipping notification creation");
                return;
            }

            // формируем сообщение в зависимости от типа уведомления
            String title;
            String message;
            
            switch (notificationType) {
                case "BOOK_CREATED":
                    title = "Новая книга доступна";
                    message = String.format("Книга \"%s\" теперь доступна в продаже", book.getTitle());
                    break;
                case "BOOK_UPDATED":
                    title = "Книга обновлена";
                    message = String.format("Книга \"%s\" была обновлена", book.getTitle());
                    break;
                case "BOOK_DELETED":
                    title = "Книга удалена";
                    message = String.format("Книга \"%s\" была удалена из каталога", book.getTitle());
                    break;
                case "PRICE_CHANGED":
                    title = "Изменение цены";
                    message = String.format("Цена на книгу \"%s\" изменена с %s до %s", 
                            book.getTitle(), oldPrice, newPrice);
                    break;
                default:
                    title = "Обновление каталога";
                    message = String.format("Книга \"%s\" была изменена", book.getTitle());
            }

            // создаем уведомления для всех пользователей, кроме текущего администратора
            int createdCount = 0;
            for (UserResponseDto user : users) {
                if (excludeUserId != null && user.getId().equals(excludeUserId)) {
                    continue; // пропускаем текущего администратора
                }

                try {
                    CreateNotificationRequestDto notificationRequest = new CreateNotificationRequestDto();
                    notificationRequest.setUserId(user.getId());
                    notificationRequest.setType(notificationType);
                    notificationRequest.setTitle(title);
                    notificationRequest.setMessage(message);
                    notificationRequest.setBookId(notificationType.equals("BOOK_DELETED") ? null : book.getId());

                    notificationServiceClient.createNotification(notificationRequest);
                    createdCount++;
                } catch (Exception e) {
                    logger.error("Failed to create notification for user {}: {}", user.getId(), e.getMessage());
                }
            }

            logger.info("Created {} notifications of type {} for book {}", createdCount, notificationType, book.getId());
        } catch (Exception e) {
            logger.error("Failed to create notifications for all users: {}", e.getMessage(), e);
            // не прерываем выполнение, если не удалось создать уведомления
        }
    }
}