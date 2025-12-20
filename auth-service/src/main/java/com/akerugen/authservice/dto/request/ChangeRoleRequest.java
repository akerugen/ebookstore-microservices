package com.akerugen.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO для запроса изменения роли пользователя
 */
public class ChangeRoleRequest {

    @Schema(description = "Username of the user whose role should be changed", example = "john_doe")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "New role for the user", example = "ADMIN", allowableValues = {"USER", "ADMIN", "SUPER_USER"})
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(USER|ADMIN|SUPER_USER)$", message = "Role must be one of: USER, ADMIN, SUPER_USER")
    private String newRole;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewRole() {
        return newRole;
    }

    public void setNewRole(String newRole) {
        this.newRole = newRole;
    }
}

