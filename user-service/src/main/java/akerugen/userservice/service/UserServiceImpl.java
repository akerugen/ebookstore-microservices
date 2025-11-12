package akerugen.userservice.service;

import akerugen.userservice.dto.request.UserRequestDto;
import akerugen.userservice.dto.response.UserResponseDto;
import akerugen.userservice.mapper.UserMapper;
import akerugen.userservice.entity.UserEntity;
import akerugen.userservice.repo.UserRepository;
import akerugen.userservice.validation.UserValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserValidator userValidator,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUser(Long id) {
        logger.info("Fetching user with id: {}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        logger.info("Creating user with username: {}", request.getUsername());
        userValidator.validate(request, null, false); // полная валидация
        UserEntity user = userMapper.toEntity(request);
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        logger.info("Updating user with id: {}", id);
        userValidator.validate(request, id, true); // частичная валидация при обновлении
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // TODO: добавить шифрование
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public void deleteAllUsers() {
        logger.info("Deleting all users");
        userRepository.deleteAll();
    }
}