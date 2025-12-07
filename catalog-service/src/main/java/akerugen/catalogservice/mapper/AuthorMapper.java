package akerugen.catalogservice.mapper;

import akerugen.catalogservice.dto.request.AuthorRequestDto;
import akerugen.catalogservice.dto.response.AuthorResponseDto;
import akerugen.catalogservice.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public AuthorResponseDto toResponseDto(Author author) {
        if (author == null) {
            return null;
        }

        AuthorResponseDto response = new AuthorResponseDto();
        response.setId(author.getId());
        response.setFirstName(author.getFirstName());
        response.setLastName(author.getLastName());
        response.setPatronymic(author.getPatronymic());
        response.setBirthDate(author.getBirthDate());
        response.setCreatedAt(author.getCreatedAt());
        response.setUpdatedAt(author.getUpdatedAt());

        return response;
    }

    public Author toEntity(AuthorRequestDto request) {
        if (request == null) {
            return null;
        }

        Author author = new Author();
        author.setFirstName(request.getFirstName());
        author.setLastName(request.getLastName());
        author.setPatronymic(request.getPatronymic());
        author.setBirthDate(request.getBirthDate());

        return author;
    }
}