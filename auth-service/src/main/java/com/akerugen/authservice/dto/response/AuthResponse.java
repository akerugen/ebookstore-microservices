package com.akerugen.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/*
    Ответ при успешной аутентификации с обоими токенами
 */
public class AuthResponse {

    @Schema(description = "Access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "Username", example = "ivan_ivanov")
    private String username;

    @Schema(description = "Expiration time in milliseconds", example = "3600000")
    private Long expiresIn;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String refreshToken, String tokenType,
                        Long userId, String username, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.userId = userId;
        this.username = username;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() { return accessToken; }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }

    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public Long getExpiresIn() { return expiresIn; }

    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
}
