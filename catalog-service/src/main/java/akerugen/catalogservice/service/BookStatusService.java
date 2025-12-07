package akerugen.catalogservice.service;

import akerugen.catalogservice.dto.request.BookStatusRequestDto;
import akerugen.catalogservice.dto.response.BookStatusResponseDto;
import java.util.List;

public interface BookStatusService {
    List<BookStatusResponseDto> getAllStatuses();

    BookStatusResponseDto getStatusById(Long id);
    BookStatusResponseDto getStatusByName(String name);
    BookStatusResponseDto createStatus(BookStatusRequestDto request);
    BookStatusResponseDto updateStatus(Long id, BookStatusRequestDto request);

    void deleteStatus(Long id);
    void deleteAllStatuses();
}