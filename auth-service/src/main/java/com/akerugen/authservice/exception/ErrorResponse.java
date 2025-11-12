package com.akerugen.authservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class ErrorResponse {

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "HTTP status code")
    private int statusCode;

    @Schema(description = "Timestamp when error occurred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Error path")
    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, int statusCode, LocalDateTime timestamp, String path) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.path = path;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public int getStatusCode() { return statusCode; }

    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public LocalDateTime getTimestamp() {return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }
}
