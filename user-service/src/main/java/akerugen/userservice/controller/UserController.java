package akerugen.userservice.controller;

import akerugen.userservice.dto.request.UserRequestDto;
import akerugen.userservice.dto.response.CheckUserExistsResponse;
import akerugen.userservice.dto.response.UserResponseDto;
import akerugen.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления пользователями
 * Endpoints:
 * - GET /api/users -> получить всех пользователей
 * - GET /api/users/{id} -> получить пользователя по ID
 * - GET /api/users/username/{username} -> получить пользователя по username
 * - POST /api/users -> создать пользователя
 * - POST /api/users/internal/create -> внутренний endpoint для auth-service
 * - GET /api/users/check?username=...&email=... -> проверить существование
 * - PATCH /api/users/{id} -> обновить профиль
 * - PATCH /api/users/username/{username} -> обновить профиль по username
 * - DELETE /api/users/{id} -> удалить пользователя
 */
@RestController
@RequestMapping("api/users")
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Получить всех пользователей
     */
    @Operation(summary = "Get all users", description = "Retrieves all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Получить пользователя по ID
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        logger.info("Received request to get user with id: {}", id);
        UserResponseDto response = userService.getUserById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Получить пользователя по username
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieves a user by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        logger.info("GET /api/users/username/{} - retrieving user", username);
        UserResponseDto user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Создать пользователя (public endpoint)
     */
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate username/email")
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto request) {
        logger.info("Received request to create user: {}", request.getUsername());
        UserResponseDto response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Создать пользователя (internal endpoint для auth-service)
     * Используется при регистрации через auth-service
     */
    @PostMapping("/internal/create")
    @Operation(summary = "Create user (internal)",
            description = "Internal endpoint used by auth-service during registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDto> createUserInternal(@Valid @RequestBody UserRequestDto request) {
        logger.info("POST /api/users/internal/create - creating user internally: {}", request.getUsername());
        UserResponseDto response = userService.createUserInternal(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Обновить профиль пользователя по ID
     */
    @Operation(summary = "Update user by ID", description = "Updates user details by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate username/email"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto request) {
        logger.info("Received request to update user with id: {}", id);
        UserResponseDto response = userService.updateUser(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Обновить профиль пользователя по username
     * Используется через API Gateway (который знает username из JWT)
     */
    @PatchMapping("/username/{username}")
    @Operation(summary = "Update user by username", description = "Updates user details by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDto> updateUserByUsername(@PathVariable String username,
                                                                @Valid @RequestBody UserRequestDto request) {
        logger.info("PATCH /api/users/username/{} - updating user", username);
        UserResponseDto response = userService.updateUserByUsername(username, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Удалить пользователя
     */
    @Operation(summary = "Delete user by ID", description = "Deletes a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Received request to delete user with id: {}", id);
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Удалить всех пользователей
     */
    @Operation(summary = "Delete all users", description = "Deletes all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All users deleted successfully")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAllUsers() {
        logger.info("Received request to delete all users");
        userService.deleteAllUsers();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Проверить существует ли пользователь
     * Используется auth-service перед регистрацией
     */
    @GetMapping("/check")
    @Operation(summary = "Check if user exists",
            description = "Checks if a user exists by username and/or email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check result returned")
    })
    public ResponseEntity<CheckUserExistsResponse> checkUserExists(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        logger.debug("GET /api/users/check - checking user existence: username={}, email={}", username, email);

        if (username == null && email == null) {
            return ResponseEntity.ok(new CheckUserExistsResponse(false, "No parameters provided"));
        }

        boolean exists = userService.userExists(username, email);
        String message = exists ? "User exists" : "User does not exist";

        return ResponseEntity.ok(new CheckUserExistsResponse(exists, message));
    }
}
