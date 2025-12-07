package akerugen.catalogservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GenreRequestDto {

    @Schema(description = "Genre name", example = "Исторический роман")
    @NotBlank(message = "Genre name cannot be blank")
    @Size(min = 1, max = 100, message = "Genre name must be between 1 and 100 characters")
    private String name;

    public GenreRequestDto() {
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}