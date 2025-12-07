package akerugen.catalogservice.service.impl;

import akerugen.catalogservice.dto.request.GenreRequestDto;
import akerugen.catalogservice.dto.response.GenreResponseDto;
import akerugen.catalogservice.entity.Genre;
import akerugen.catalogservice.mapper.GenreMapper;
import akerugen.catalogservice.repo.GenreRepository;
import akerugen.catalogservice.service.GenreService;
import akerugen.catalogservice.validator.GenreValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GenreServiceImpl implements GenreService {

    private static final Logger logger = LogManager.getLogger(GenreServiceImpl.class);

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final GenreValidator genreValidator;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository,
                            GenreMapper genreMapper,
                            GenreValidator genreValidator) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
        this.genreValidator = genreValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreResponseDto> getAllGenres() {
        logger.info("Fetching all genres");
        List<Genre> genres = genreRepository.findAll();
        return genres.stream()
                .map(genreMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponseDto getGenreById(Long id) {
        logger.info("Fetching genre with id: {}", id);
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found with id: " + id));
        return genreMapper.toResponseDto(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponseDto getGenreByName(String name) {
        logger.info("Fetching genre by name: {}", name);
        Genre genre = genreRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Genre not found with name: " + name));
        return genreMapper.toResponseDto(genre);
    }

    @Override
    public GenreResponseDto createGenre(GenreRequestDto request) {
        logger.info("Creating genre: {}", request.getName());
        genreValidator.validate(request);

        Genre genre = genreMapper.toEntity(request);
        genre.setCreatedAt(LocalDateTime.now());

        Genre savedGenre = genreRepository.save(genre);
        logger.info("Genre created with id: {}", savedGenre.getId());
        return genreMapper.toResponseDto(savedGenre);
    }

    @Override
    public GenreResponseDto updateGenre(Long id, GenreRequestDto request) {
        logger.info("Updating genre with id: {}", id);

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found with id: " + id));

        genre.setName(request.getName());
        genre.setUpdatedAt(LocalDateTime.now());

        Genre updatedGenre = genreRepository.save(genre);
        logger.info("Genre updated with id: {}", id);
        return genreMapper.toResponseDto(updatedGenre);
    }

    @Override
    public void deleteGenre(Long id) {
        logger.info("Deleting genre with id: {}", id);
        if (!genreRepository.existsById(id)) {
            throw new RuntimeException("Genre not found with id: " + id);
        }
        genreRepository.deleteById(id);
        logger.info("Genre deleted with id: {}", id);
    }

    @Override
    public void deleteAllGenres() {
        logger.info("Deleting all genres");
        genreRepository.deleteAll();
    }
}
