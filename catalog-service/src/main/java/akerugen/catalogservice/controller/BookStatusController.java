package akerugen.catalogservice.controller;

import akerugen.catalogservice.dto.request.BookStatusRequestDto;
import akerugen.catalogservice.dto.response.BookStatusResponseDto;
import akerugen.catalogservice.service.BookStatusService;
import akerugen.catalogservice.util.RoleAuthorizationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RoleAuthorizationUtil roleAuthorizationUtil;

    @Autowired
    public BookStatusController(BookStatusService bookStatusService, RoleAuthorizationUtil roleAuthorizationUtil) {
        this.bookStatusService = bookStatusService;
        this.roleAuthorizationUtil = roleAuthorizationUtil;
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
    @Operation(summary = "Create a new status", description = "Creates a new book status (requires ADMIN or SUPER_USER role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Status created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or status already exists"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<BookStatusResponseDto> createStatus(
            @Valid @RequestBody BookStatusRequestDto request,
            HttpServletRequest httpRequest) {

        logger.info("POST /api/catalog/book-statuses - creating status: {}", request.getName());

        if (!roleAuthorizationUtil.isAdminOrSuperUser(httpRequest)) {
            logger.warn("User does not have ADMIN or SUPER_USER role to create status");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        BookStatusResponseDto status = bookStatusService.createStatus(request);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update status", description = "Updates an existing book status (requires ADMIN or SUPER_USER role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Status not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<BookStatusResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookStatusRequestDto request,
            HttpServletRequest httpRequest) {

        logger.info("PUT /api/catalog/book-statuses/{} - updating status", id);

        if (!roleAuthorizationUtil.isAdminOrSuperUser(httpRequest)) {
            logger.warn("User does not have ADMIN or SUPER_USER role to update status");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        BookStatusResponseDto status = bookStatusService.updateStatus(id, request);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete status", description = "Deletes a book status (requires ADMIN or SUPER_USER role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Status not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<Void> deleteStatus(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        logger.info("DELETE /api/catalog/book-statuses/{} - deleting status", id);

        if (!roleAuthorizationUtil.isAdminOrSuperUser(httpRequest)) {
            logger.warn("User does not have ADMIN or SUPER_USER role to delete status");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        bookStatusService.deleteStatus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Operation(summary = "Delete all statuses", description = "Deletes all book statuses (requires ADMIN or SUPER_USER role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All statuses deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<Void> deleteAllStatuses(HttpServletRequest httpRequest) {
        logger.info("DELETE /api/catalog/book-statuses - deleting all statuses");

        if (!roleAuthorizationUtil.isAdminOrSuperUser(httpRequest)) {
            logger.warn("User does not have ADMIN or SUPER_USER role to delete all statuses");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        bookStatusService.deleteAllStatuses();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}