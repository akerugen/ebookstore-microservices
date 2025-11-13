package akerugen.userservice.service;

import akerugen.userservice.dto.request.UserRequestDto;
import akerugen.userservice.dto.response.UserResponseDto;
import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long id);
    UserResponseDto getUserByUsername(String username);
    UserResponseDto createUser(UserRequestDto request);
    UserResponseDto createUserInternal(UserRequestDto request);
    UserResponseDto updateUser(Long id, UserRequestDto request);
    UserResponseDto updateUserByUsername(String username, UserRequestDto request);

    void deleteUser(Long id);
    void deleteAllUsers();

    boolean userExists(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
