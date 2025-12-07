package akerugen.catalogservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookResponseDto {

    @Schema(description = "Book ID", example = "1")
    private Long id;

    @Schema(description = "Book title", example = "Война и мир")
    private String title;

    @Schema(description = "Book price", example = "599.99")
    private BigDecimal price;

    @Schema(description = "Book description")
    private String description;

    @Schema(description = "Author information")
    private AuthorResponseDto author;

    @Schema(description = "Genre information")
    private GenreResponseDto genre;

    @Schema(description = "Book status")
    private BookStatusResponseDto bookStatus;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;

    public BookResponseDto() {
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public BigDecimal getPrice() { return price; }

    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public AuthorResponseDto getAuthor() { return author; }

    public void setAuthor(AuthorResponseDto author) { this.author = author; }

    public GenreResponseDto getGenre() { return genre; }

    public void setGenre(GenreResponseDto genre) { this.genre = genre; }

    public BookStatusResponseDto getBookStatus() { return bookStatus; }

    public void setBookStatus(BookStatusResponseDto bookStatus) { this.bookStatus = bookStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}