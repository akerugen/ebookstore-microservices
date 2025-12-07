package akerugen.catalogservice.controller;

import akerugen.catalogservice.dto.request.BookStatusRequestDto;
import akerugen.catalogservice.dto.response.BookStatusResponseDto;
import akerugen.catalogservice.service.BookStatusService;
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
@RequestMapping("/api/catalog/book-statuses")
@Tag(name = "Book Statuses Management", description = "Operations related to book status management")
public class BookStatusController {

    private static final Logger logger = LogManager.getLogger(BookStatusController.class);
    private final BookStatusService bookStatusService;

    @Autowired
    public BookStatusController(BookStatusService bookStatusService) {
        this.bookStatusService = bookStatusService;
    }

    @GetMapping
    @Operation(summary = "Get all book statuses", description = "Retrieves all book statuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statuses retrieved successfully")
    })
    public ResponseEntity<List<BookStatusResponseDto>> getAllStatuses() {
        logger.info("GET /api/catalog/book-statuses - retrieving all book statuses");
        List<BookStatusResponseDto> statuses = bookStatusService.getAllStatuses();
        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get status by ID", description = "Retrieves a status by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status found"),
            @ApiResponse(responseCode = "404", description = "Status not found")
    })
    public ResponseEntity<BookStatusResponseDto> getStatusById(@PathVariable Long id) {
        logger.info("GET /api/catalog/book-statuses/{} - retrieving status", id);
        BookStatusResponseDto status = bookStatusService.getStatusById(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get status by name", description = "Retrieves a status by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status found"),
            @ApiResponse(responseCode = "404", description = "Status not found")
    })
    public ResponseEntity<BookStatusResponseDto> getStatusByName(@PathVariable String name) {
        logger.info("GET /api/catalog/book-statuses/name/{} - retrieving status by name", name);
        BookStatusResponseDto status = bookStatusService.getStatusByName(name);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new status", description = "Creates a new book status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Status created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or status already exists")
    })
    public ResponseEntity<BookStatusResponseDto> createStatus(@Valid @RequestBody BookStatusRequestDto request) {
        logger.info("POST /api/catalog/book-statuses - creating status: {}", request.getName());
        BookStatusResponseDto status = bookStatusService.createStatus(request);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update status", description = "Updates an existing book status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Status not found")
    })
    public ResponseEntity<BookStatusResponseDto> updateStatus(@PathVariable Long id,
                                                              @Valid @RequestBody BookStatusRequestDto request) {
        logger.info("PUT /api/catalog/book-statuses/{} - updating status", id);
        BookStatusResponseDto status = bookStatusService.updateStatus(id, request);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete status", description = "Deletes a book status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Status not found")
    })
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        logger.info("DELETE /api/catalog/book-statuses/{} - deleting status", id);
        bookStatusService.deleteStatus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Operation(summary = "Delete all statuses", description = "Deletes all book statuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All statuses deleted successfully")
    })
    public ResponseEntity<Void> deleteAllStatuses() {
        logger.info("DELETE /api/catalog/book-statuses - deleting all statuses");
        bookStatusService.deleteAllStatuses();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}