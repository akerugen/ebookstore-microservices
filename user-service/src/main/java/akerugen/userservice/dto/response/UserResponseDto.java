package akerugen.userservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class UserResponseDto {

    @Schema(description = "Unique ID of the user", example = "1")
    private Long id;

    @Schema(description = "Unique username for user", example = "ivan_ivanov")
    private String username;

    @Schema(description = "User's email address", example = "ivan_ivanov@example.com")
    private String email;

    @Schema(description = "Timestamp when the user was created", example = "2025-07-24T12:00:00")
    private LocalDateTime createdAt;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
