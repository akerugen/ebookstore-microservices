package akerugen.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UserRequestDto {

    @Schema(description = "Username of the user", example = "ivanov")
    @Size(max = 50, message = "Username must be at most 50 characters")
    private String username;

    @Schema(description = "Email address of the user", example = "ivan_ivanov@example.com")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @Schema(description = "First name", example = "Ivan")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @Schema(description = "Last name", example = "Ivanov")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @Schema(description = "Date of birth", example = "1990-01-15")
    private LocalDate dateOfBirth;

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public  String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}
