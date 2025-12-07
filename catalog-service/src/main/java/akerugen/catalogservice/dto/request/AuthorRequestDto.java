package akerugen.catalogservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class AuthorRequestDto {

    @Schema(description = "First name", example = "Лев")
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @Schema(description = "Last name", example = "Толстой")
    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @Schema(description = "Patronymic (отчество)", example = "Николаевич")
    @Size(max = 100, message = "Patronymic must be at most 100 characters")
    private String patronymic;

    @Schema(description = "Birth date", example = "1828-09-09")
    private LocalDate birthDate;

    public AuthorRequestDto() {
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPatronymic() { return patronymic; }

    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }

    public LocalDate getBirthDate() { return birthDate; }

    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}