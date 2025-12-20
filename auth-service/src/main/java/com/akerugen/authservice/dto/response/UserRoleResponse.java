package com.akerugen.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для ответа с ролью пользователя
 */
public class UserRoleResponse {

    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Schema(description = "User role", example = "ADMIN", allowableValues = {"USER", "ADMIN", "SUPER_USER"})
    private String role;

    public UserRoleResponse() {
    }

    public UserRoleResponse(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

