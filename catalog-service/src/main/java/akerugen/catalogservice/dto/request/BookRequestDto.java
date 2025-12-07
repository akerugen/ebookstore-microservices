package akerugen.catalogservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class BookRequestDto {

    @Schema(description = "Book title", example = "Война и мир")
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Schema(description = "Book price", example = "599.99")
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Schema(description = "Book description", example = "Исторический роман...")
    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    @Schema(description = "Author ID", example = "1")
    @NotNull(message = "Author ID cannot be null")
    private Long authorId;

    @Schema(description = "Genre ID", example = "1")
    @NotNull(message = "Genre ID cannot be null")
    private Long genreId;

    @Schema(description = "Book Status ID", example = "1")
    @NotNull(message = "Book Status ID cannot be null")
    private Long bookStatusId;

    public BookRequestDto() {
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public BigDecimal getPrice() { return price; }

    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Long getAuthorId() { return authorId; }

    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Long getGenreId() { return genreId; }

    public void setGenreId(Long genreId) { this.genreId = genreId; }

    public Long getBookStatusId() { return bookStatusId; }

    public void setBookStatusId(Long bookStatusId) { this.bookStatusId = bookStatusId; }
}