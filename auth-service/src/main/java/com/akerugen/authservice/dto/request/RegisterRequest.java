package com.akerugen.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
    DTO для запроса регистрации. Содержит валидацию через аннотации.
 */
public class RegisterRequest {

    @Schema(description = "Username", example = "ivan_ivanov")
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Email address", example = "ivan@example.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(description = "First name", example = "Ivan")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @Schema(description = "Last name", example = "Ivanov")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @Schema(description = "Password", example = "securePassword123")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @Schema(description = "Password confirmation", example = "securePassword123")
    @NotBlank(message = "Password confirmation cannot be blank")
    private String confirmPassword;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public RegisterRequest(String username, String email, String firstName, String lastName,
                           String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }

    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
