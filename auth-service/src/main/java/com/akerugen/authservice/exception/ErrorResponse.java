package com.akerugen.authservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    @Schema(description = "HTTP status code")
    private int statusCode;

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "Error path")
    private String path;

    @Schema(description = "Timestamp when error occurred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "List of errors")
    private Map<String, String> errors;

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode, String message, String path,  LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    public ErrorResponse(int statusCode, String message, String path,  LocalDateTime timestamp, Map<String, String> errors) {
        this.statusCode = statusCode;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public int getStatusCode() { return statusCode; }

    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public LocalDateTime getTimestamp() {return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    public Map<String, String> getErrors() { return errors; }

    public void setErrors(Map<String, String> errors) { this.errors = errors; }
}
