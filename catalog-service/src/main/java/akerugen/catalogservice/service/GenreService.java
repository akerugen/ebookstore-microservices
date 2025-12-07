package akerugen.catalogservice.service;

import akerugen.catalogservice.dto.request.GenreRequestDto;
import akerugen.catalogservice.dto.response.GenreResponseDto;
import java.util.List;

public interface GenreService {
    List<GenreResponseDto> getAllGenres();

    GenreResponseDto getGenreById(Long id);
    GenreResponseDto getGenreByName(String name);
    GenreResponseDto createGenre(GenreRequestDto request);
    GenreResponseDto updateGenre(Long id, GenreRequestDto request);

    void deleteGenre(Long id);
    void deleteAllGenres();
}
