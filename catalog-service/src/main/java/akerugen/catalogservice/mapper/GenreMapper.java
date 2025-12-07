package akerugen.catalogservice.mapper;

import akerugen.catalogservice.dto.request.GenreRequestDto;
import akerugen.catalogservice.dto.response.GenreResponseDto;
import akerugen.catalogservice.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreResponseDto toResponseDto(Genre genre) {
        if (genre == null) {
            return null;
        }

        GenreResponseDto response = new GenreResponseDto();
        response.setId(genre.getId());
        response.setName(genre.getName());
        response.setCreatedAt(genre.getCreatedAt());
        response.setUpdatedAt(genre.getUpdatedAt());

        return response;
    }

    public Genre toEntity(GenreRequestDto request) {
        if (request == null) {
            return null;
        }

        Genre genre = new Genre();
        genre.setName(request.getName());

        return genre;
    }
}
