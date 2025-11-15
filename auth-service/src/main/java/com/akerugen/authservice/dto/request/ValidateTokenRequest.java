package com.akerugen.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO для валидации токена
 */
public class ValidateTokenRequest {

    @Schema(description = "Access token to validate", example = "eyJhbGciOiJIUzUxMiJ9...")
    @NotBlank(message = "token cannot be blank")
    private String token;

    public ValidateTokenRequest() {
    }

    public ValidateTokenRequest(String token) { this.token = token; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }
}
