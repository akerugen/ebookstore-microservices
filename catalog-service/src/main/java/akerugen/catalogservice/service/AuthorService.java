package akerugen.catalogservice.service;

import akerugen.catalogservice.dto.request.AuthorRequestDto;
import akerugen.catalogservice.dto.response.AuthorResponseDto;
import java.util.List;

public interface AuthorService {
    List<AuthorResponseDto> getAllAuthors();
    AuthorResponseDto getAuthorById(Long id);
    List<AuthorResponseDto> searchByName(String name);

    AuthorResponseDto createAuthor(AuthorRequestDto request);
    AuthorResponseDto updateAuthor(Long id, AuthorRequestDto request);

    void deleteAuthor(Long id);
    void deleteAllAuthors();
}
