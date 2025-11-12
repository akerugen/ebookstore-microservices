package com.akerugen.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/*
    DTO для запроса входа по username и по email.
 */
public class LoginRequest {

    @Schema(description = "Username or email", example = "ivan_ivanov")
    @NotBlank(message = "Username or email cannot be blank")
    private String usernameOrEmail;

    @Schema(description = "Password", example = "securePassword123")
    @NotBlank(message = "Password cannot be blank")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public String getUsernameOrEmail() { return usernameOrEmail; }

    public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
