package akerugen.catalogservice.mapper;

import akerugen.catalogservice.dto.request.BookRequestDto;
import akerugen.catalogservice.dto.response.BookResponseDto;
import akerugen.catalogservice.entity.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    private static final Logger logger = LogManager.getLogger(BookMapper.class);

    private final AuthorMapper authorMapper;
    private final GenreMapper genreMapper;
    private final BookStatusMapper bookStatusMapper;

    @Autowired
    public BookMapper(AuthorMapper authorMapper, GenreMapper genreMapper, BookStatusMapper bookStatusMapper) {
        this.authorMapper = authorMapper;
        this.genreMapper = genreMapper;
        this.bookStatusMapper = bookStatusMapper;
    }

    public BookResponseDto toResponseDto(Book book) {
        if (book == null) {
            return null;
        }

        BookResponseDto response = new BookResponseDto();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setPrice(book.getPrice());
        response.setDescription(book.getDescription());
        response.setAuthor(authorMapper.toResponseDto(book.getAuthor()));
        response.setGenre(genreMapper.toResponseDto(book.getGenre()));
        response.setBookStatus(bookStatusMapper.toResponseDto(book.getBookStatus()));
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());

        return response;
    }

    public Book toEntity(BookRequestDto request) {
        if (request == null) {
            return null;
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());

        return book;
    }
}