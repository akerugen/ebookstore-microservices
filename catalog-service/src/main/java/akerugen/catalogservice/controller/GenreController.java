package akerugen.catalogservice.controller;

import akerugen.catalogservice.dto.request.GenreRequestDto;
import akerugen.catalogservice.dto.response.GenreResponseDto;
import akerugen.catalogservice.service.GenreService;
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
@RequestMapping("/api/catalog/genres")
@Tag(name = "Genres Management", description = "Operations related to genre management")
public class GenreController {

    private static final Logger logger = LogManager.getLogger(GenreController.class);
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    @Operation(summary = "Get all genres", description = "Retrieves all genres")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genres retrieved successfully")
    })
    public ResponseEntity<List<GenreResponseDto>> getAllGenres() {
        logger.info("GET /api/catalog/genres - retrieving all genres");
        List<GenreResponseDto> genres = genreService.getAllGenres();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get genre by ID", description = "Retrieves a genre by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre found"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public ResponseEntity<GenreResponseDto> getGenreById(@PathVariable Long id) {
        logger.info("GET /api/catalog/genres/{} - retrieving genre", id);
        GenreResponseDto genre = genreService.getGenreById(id);
        return new ResponseEntity<>(genre, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get genre by name", description = "Retrieves a genre by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre found"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public ResponseEntity<GenreResponseDto> getGenreByName(@PathVariable String name) {
        logger.info("GET /api/catalog/genres/name/{} - retrieving genre by name", name);
        GenreResponseDto genre = genreService.getGenreByName(name);
        return new ResponseEntity<>(genre, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new genre", description = "Creates a new genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Genre created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or genre already exists")
    })
    public ResponseEntity<GenreResponseDto> createGenre(@Valid @RequestBody GenreRequestDto request) {
        logger.info("POST /api/catalog/genres - creating genre: {}", request.getName());
        GenreResponseDto genre = genreService.createGenre(request);
        return new ResponseEntity<>(genre, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update genre", description = "Updates an existing genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public ResponseEntity<GenreResponseDto> updateGenre(@PathVariable Long id,
                                                        @Valid @RequestBody GenreRequestDto request) {
        logger.info("PUT /api/catalog/genres/{} - updating genre", id);
        GenreResponseDto genre = genreService.updateGenre(id, request);
        return new ResponseEntity<>(genre, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete genre", description = "Deletes a genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Genre deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        logger.info("DELETE /api/catalog/genres/{} - deleting genre", id);
        genreService.deleteGenre(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Operation(summary = "Delete all genres", description = "Deletes all genres")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All genres deleted successfully")
    })
    public ResponseEntity<Void> deleteAllGenres() {
        logger.info("DELETE /api/catalog/genres - deleting all genres");
        genreService.deleteAllGenres();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
