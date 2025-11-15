package com.akerugen.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * DTO для ответа при logout
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogoutResponse {

    private int statusCode;
    private String message;
    private String username;
    private LocalDateTime timestamp;

    public LogoutResponse() {
    }

    public LogoutResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public LogoutResponse(int statusCode, String message, String username) {
        this.statusCode = statusCode;
        this.message = message;
        this.username = username;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatusCode() { return statusCode; }

    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
