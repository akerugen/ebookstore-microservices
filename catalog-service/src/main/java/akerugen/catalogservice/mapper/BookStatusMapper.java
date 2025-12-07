package akerugen.catalogservice.mapper;

import akerugen.catalogservice.dto.request.BookStatusRequestDto;
import akerugen.catalogservice.dto.response.BookStatusResponseDto;
import akerugen.catalogservice.entity.BookStatus;
import org.springframework.stereotype.Component;

@Component
public class BookStatusMapper {

    public BookStatusResponseDto toResponseDto(BookStatus bookStatus) {
        if (bookStatus == null) {
            return null;
        }

        BookStatusResponseDto response = new BookStatusResponseDto();
        response.setId(bookStatus.getId());
        response.setName(bookStatus.getName());
        response.setCreatedAt(bookStatus.getCreatedAt());
        response.setUpdatedAt(bookStatus.getUpdatedAt());

        return response;
    }

    public BookStatus toEntity(BookStatusRequestDto request) {
        if (request == null) {
            return null;
        }

        BookStatus bookStatus = new BookStatus();
        bookStatus.setName(request.getName());

        return bookStatus;
    }
}
