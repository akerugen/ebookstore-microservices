package akerugen.userservice.mapper;

import akerugen.userservice.dto.request.UserRequestDto;
import akerugen.userservice.dto.response.UserResponseDto;
import akerugen.userservice.entity.UserEntity;
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
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    public UserEntity toEntity(UserRequestDto request) {
        if (request == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setEmail(request.getEmail());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setDateOfBirth(request.getDateOfBirth());
        return user;
    }
}
