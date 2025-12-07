package akerugen.catalogservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BookStatusRequestDto {

    @Schema(description = "Status name", example = "Available")
    @NotBlank(message = "Status name cannot be blank")
    @Size(min = 1, max = 50, message = "Status name must be between 1 and 50 characters")
    private String name;

    public BookStatusRequestDto() {
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}