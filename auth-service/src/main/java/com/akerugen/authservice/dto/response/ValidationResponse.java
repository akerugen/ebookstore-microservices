package com.akerugen.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
/*
    Ответ при валидации токена (при проверке в gateway или других сервисах)
 */
public class ValidationResponse {

    @Schema(description = "Is token valid", example = "true")
    private Boolean valid;

    @Schema(description = "User ID from token", example = "1")
    private Long userId;

    @Schema(description = "Username from token", example = "ivan_ivanov")
    private String username;

    public ValidationResponse() {
    }

    public ValidationResponse(Boolean valid, Long userId, String username) {
        this.valid = valid;
        this.userId = userId;
        this.username = username;
    }

    public Boolean getValid() { return valid; }

    public void setValid(Boolean valid) { this.valid = valid; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
}
