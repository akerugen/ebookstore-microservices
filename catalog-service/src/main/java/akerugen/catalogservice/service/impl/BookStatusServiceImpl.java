package akerugen.catalogservice.service.impl;

import akerugen.catalogservice.dto.request.BookStatusRequestDto;
import akerugen.catalogservice.dto.response.BookStatusResponseDto;
import akerugen.catalogservice.entity.BookStatus;
import akerugen.catalogservice.mapper.BookStatusMapper;
import akerugen.catalogservice.repo.BookStatusRepository;
import akerugen.catalogservice.service.BookStatusService;
import akerugen.catalogservice.validator.BookStatusValidator;
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
public class BookStatusServiceImpl implements BookStatusService {

    private static final Logger logger = LogManager.getLogger(BookStatusServiceImpl.class);

    private final BookStatusRepository bookStatusRepository;
    private final BookStatusMapper bookStatusMapper;
    private final BookStatusValidator bookStatusValidator;

    @Autowired
    public BookStatusServiceImpl(BookStatusRepository bookStatusRepository,
                                 BookStatusMapper bookStatusMapper,
                                 BookStatusValidator bookStatusValidator) {
        this.bookStatusRepository = bookStatusRepository;
        this.bookStatusMapper = bookStatusMapper;
        this.bookStatusValidator = bookStatusValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookStatusResponseDto> getAllStatuses() {
        logger.info("Fetching all book statuses");
        List<BookStatus> statuses = bookStatusRepository.findAll();
        return statuses.stream()
                .map(bookStatusMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookStatusResponseDto getStatusById(Long id) {
        logger.info("Fetching book status with id: {}", id);
        BookStatus status = bookStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookStatus not found with id: " + id));
        return bookStatusMapper.toResponseDto(status);
    }

    @Override
    @Transactional(readOnly = true)
    public BookStatusResponseDto getStatusByName(String name) {
        logger.info("Fetching book status by name: {}", name);
        BookStatus status = bookStatusRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("BookStatus not found with name: " + name));
        return bookStatusMapper.toResponseDto(status);
    }

    @Override
    public BookStatusResponseDto createStatus(BookStatusRequestDto request) {
        logger.info("Creating book status: {}", request.getName());
        bookStatusValidator.validate(request);

        BookStatus status = bookStatusMapper.toEntity(request);
        status.setCreatedAt(LocalDateTime.now());

        BookStatus savedStatus = bookStatusRepository.save(status);
        logger.info("BookStatus created with id: {}", savedStatus.getId());
        return bookStatusMapper.toResponseDto(savedStatus);
    }

    @Override
    public BookStatusResponseDto updateStatus(Long id, BookStatusRequestDto request) {
        logger.info("Updating book status with id: {}", id);

        BookStatus status = bookStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookStatus not found with id: " + id));

        status.setName(request.getName());
        status.setUpdatedAt(LocalDateTime.now());

        BookStatus updatedStatus = bookStatusRepository.save(status);
        logger.info("BookStatus updated with id: {}", id);
        return bookStatusMapper.toResponseDto(updatedStatus);
    }

    @Override
    public void deleteStatus(Long id) {
        logger.info("Deleting book status with id: {}", id);
        if (!bookStatusRepository.existsById(id)) {
            throw new RuntimeException("BookStatus not found with id: " + id);
        }
        bookStatusRepository.deleteById(id);
        logger.info("BookStatus deleted with id: {}", id);
    }

    @Override
    public void deleteAllStatuses() {
        logger.info("Deleting all book statuses");
        bookStatusRepository.deleteAll();
    }
}