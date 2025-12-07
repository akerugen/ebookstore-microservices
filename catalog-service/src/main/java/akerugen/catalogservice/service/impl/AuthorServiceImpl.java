package akerugen.catalogservice.service.impl;

import akerugen.catalogservice.dto.request.AuthorRequestDto;
import akerugen.catalogservice.dto.response.AuthorResponseDto;
import akerugen.catalogservice.entity.Author;
import akerugen.catalogservice.mapper.AuthorMapper;
import akerugen.catalogservice.repo.AuthorRepository;
import akerugen.catalogservice.service.AuthorService;
import akerugen.catalogservice.validator.AuthorValidator;
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
public class AuthorServiceImpl implements AuthorService {

    private static final Logger logger = LogManager.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final AuthorValidator authorValidator;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository,
                             AuthorMapper authorMapper,
                             AuthorValidator authorValidator) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.authorValidator = authorValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponseDto> getAllAuthors() {
        logger.info("Fetching all authors");
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .map(authorMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponseDto getAuthorById(Long id) {
        logger.info("Fetching author with id: {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        return authorMapper.toResponseDto(author);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponseDto> searchByName(String name) {
        logger.info("Searching authors by name: {}", name);
        List<Author> authors = authorRepository.findByFullNameContaining(name);
        return authors.stream()
                .map(authorMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorResponseDto createAuthor(AuthorRequestDto request) {
        logger.info("Creating author: {} {}", request.getFirstName(), request.getLastName());
        authorValidator.validate(request);

        Author author = authorMapper.toEntity(request);
        author.setCreatedAt(LocalDateTime.now());

        Author savedAuthor = authorRepository.save(author);
        logger.info("Author created with id: {}", savedAuthor.getId());
        return authorMapper.toResponseDto(savedAuthor);
    }

    @Override
    public AuthorResponseDto updateAuthor(Long id, AuthorRequestDto request) {
        logger.info("Updating author with id: {}", id);
        authorValidator.validate(request);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));

        author.setFirstName(request.getFirstName());
        author.setLastName(request.getLastName());
        author.setPatronymic(request.getPatronymic());
        author.setBirthDate(request.getBirthDate());
        author.setUpdatedAt(LocalDateTime.now());

        Author updatedAuthor = authorRepository.save(author);
        logger.info("Author updated with id: {}", id);
        return authorMapper.toResponseDto(updatedAuthor);
    }

    @Override
    public void deleteAuthor(Long id) {
        logger.info("Deleting author with id: {}", id);
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
        logger.info("Author deleted with id: {}", id);
    }

    @Override
    public void deleteAllAuthors() {
        logger.info("Deleting all authors");
        authorRepository.deleteAll();
    }
}
