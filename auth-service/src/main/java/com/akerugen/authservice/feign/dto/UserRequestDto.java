package com.akerugen.authservice.feign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для отправки данных пользователя в user-service
 * Пароль остаётся в auth-service и не отправляется
 */
public class UserRequestDto {

    @Schema(description = "Username", example = "ivan_ivanov")
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Email", example = "ivan@example.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(description = "First name", example = "Ivan")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @Schema(description = "Last name", example = "Ivanov")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;


    public UserRequestDto() {
    }

    public UserRequestDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public UserRequestDto(String username, String email, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }
}