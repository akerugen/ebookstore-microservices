package akerugen.catalogservice.service;

import akerugen.catalogservice.dto.request.BookRequestDto;
import akerugen.catalogservice.dto.response.BookResponseDto;
import java.util.List;

public interface BookService {
    List<BookResponseDto> getAllBooks();
    BookResponseDto getBookById(Long id);

    List<BookResponseDto> getBooksByAuthor(Long authorId);
    List<BookResponseDto> getBooksByGenre(Long genreId);
    List<BookResponseDto> getBooksByStatus(Long statusId);
    List<BookResponseDto> searchByTitle(String title);

    BookResponseDto createBook(BookRequestDto request);
    BookResponseDto updateBook(Long id, BookRequestDto request);

    void deleteBook(Long id);
    void deleteAllBooks();
}
