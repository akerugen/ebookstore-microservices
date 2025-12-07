package akerugen.catalogservice.controller;

import akerugen.catalogservice.dto.request.AuthorRequestDto;
import akerugen.catalogservice.dto.response.AuthorResponseDto;
import akerugen.catalogservice.service.AuthorService;
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
@RequestMapping("/api/catalog/authors")
@Tag(name = "Authors Management", description = "Operations related to author management")
public class AuthorController {

    private static final Logger logger = LogManager.getLogger(AuthorController.class);
    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    @Operation(summary = "Get all authors", description = "Retrieves all authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authors retrieved successfully")
    })
    public ResponseEntity<List<AuthorResponseDto>> getAllAuthors() {
        logger.info("GET /api/catalog/authors - retrieving all authors");
        List<AuthorResponseDto> authors = authorService.getAllAuthors();
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Retrieves an author by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author found"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable Long id) {
        logger.info("GET /api/catalog/authors/{} - retrieving author", id);
        AuthorResponseDto author = authorService.getAuthorById(id);
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Operation(summary = "Search authors by name", description = "Searches authors by full name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned")
    })
    public ResponseEntity<List<AuthorResponseDto>> searchByName(@RequestParam String name) {
        logger.info("GET /api/catalog/authors/search - searching by name: {}", name);
        List<AuthorResponseDto> authors = authorService.searchByName(name);
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new author", description = "Creates a new author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Author created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<AuthorResponseDto> createAuthor(@Valid @RequestBody AuthorRequestDto request) {
        logger.info("POST /api/catalog/authors - creating author: {} {}", request.getFirstName(), request.getLastName());
        AuthorResponseDto author = authorService.createAuthor(request);
        return new ResponseEntity<>(author, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update author", description = "Updates an existing author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<AuthorResponseDto> updateAuthor(@PathVariable Long id,
                                                          @Valid @RequestBody AuthorRequestDto request) {
        logger.info("PUT /api/catalog/authors/{} - updating author", id);
        AuthorResponseDto author = authorService.updateAuthor(id, request);
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete author", description = "Deletes an author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        logger.info("DELETE /api/catalog/authors/{} - deleting author", id);
        authorService.deleteAuthor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Operation(summary = "Delete all authors", description = "Deletes all authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All authors deleted successfully")
    })
    public ResponseEntity<Void> deleteAllAuthors() {
        logger.info("DELETE /api/catalog/authors - deleting all authors");
        authorService.deleteAllAuthors();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}