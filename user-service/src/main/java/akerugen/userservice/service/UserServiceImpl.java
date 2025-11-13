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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        logger.info("Fetching user with id: {}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        logger.info("Fetching user with username: {}", username);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        logger.info("Creating user with username: {}", request.getUsername());
        userValidator.validate(request, null, false); // полная валидация

        // Проверка уникальности
        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new RuntimeException("User with this username or email already exists");
        }

        UserEntity user = userMapper.toEntity(request);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User created with id: {}", user.getId());
        return userMapper.toResponseDto(user);
    }

    /**
     * internal endpoint для auth-service
     * Используется только при регистрации через auth-service
     * Не требует дополнительной валидации
     */
    @Override
    public UserResponseDto createUserInternal(UserRequestDto request) {
        logger.info("Creating user internally from auth-service: {}", request.getUsername());

        // Проверка уникальности (критично!)
        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new RuntimeException("User with this username or email already exists");
        }

        UserEntity user = userMapper.toEntity(request);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User created internally with id: {}", user.getId());
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        logger.info("Updating user with id: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Обновляем ТОЛЬКО поля профиля
        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            user.setFirstName(request.getFirstName());
            logger.debug("Updated firstName to: {}", request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            user.setLastName(request.getLastName());
            logger.debug("Updated lastName to: {}", request.getLastName());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User updated successfully with id: {}", id);
        return userMapper.toResponseDto(user);
    }

    /**
     * Обновить профиль пользователя по username
     * Используется для обновления через Gateway (который знает только username) TODO: может быть стоит поправить
     */
    @Override
    public UserResponseDto updateUserByUsername(String username, UserRequestDto request) {
        logger.info("Updating user with username: {}", username);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Обновляем ТОЛЬКО поля профиля
        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            user.setFirstName(request.getFirstName());
            logger.debug("Updated firstName to: {}", request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            user.setLastName(request.getLastName());
            logger.debug("Updated lastName to: {}", request.getLastName());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User updated successfully with username: {}", username);
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

    /**
     * Проверить существует ли пользователь (для auth-service)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean userExists(String username, String email) {
        logger.debug("Checking if user exists: username={}, email={}", username, email);
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        logger.debug("Checking if username exists: {}", username);
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        logger.debug("Checking if email exists: {}", email);
        return userRepository.existsByEmail(email);
    }
}