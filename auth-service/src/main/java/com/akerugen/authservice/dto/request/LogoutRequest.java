package com.akerugen.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO для logout запроса
 */
public class LogoutRequest {

    @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzUxMiJ9...")
    @NotBlank(message = "refreshToken cannot be blank")
    private String refreshToken;

    public LogoutRequest() {
    }

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() { return refreshToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
