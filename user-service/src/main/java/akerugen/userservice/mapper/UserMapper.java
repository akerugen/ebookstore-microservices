package akerugen.userservice.mapper;

import akerugen.userservice.dto.request.UserRequestDto;
import akerugen.userservice.dto.response.UserResponseDto;
import akerugen.userservice.model.UserEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private static final Logger logger = LogManager.getLogger(UserMapper.class);

    public UserResponseDto toResponseDto(UserEntity user) {
        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public UserEntity toEntity(UserRequestDto request) {
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // TODO: добавить шифрование
        user.setCreatedAt(java.time.LocalDateTime.now());
        return user;
    }
}
