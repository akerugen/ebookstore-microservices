package akerugen.userservice.service;

import akerugen.userservice.dto.request.UserRequestDto;
import akerugen.userservice.dto.response.UserResponseDto;
import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();
    UserResponseDto getUser(Long id);
    UserResponseDto createUser(UserRequestDto request);
    UserResponseDto updateUser(Long id, UserRequestDto request);
    void deleteUser(Long id);
    void deleteAllUsers();
}
