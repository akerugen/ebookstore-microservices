package akerugen.catalogservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int statusCode;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> errors;

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode, String message, String path, LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    public ErrorResponse(int statusCode, String message, String path, LocalDateTime timestamp,
                         Map<String, String> errors) {
        this.statusCode = statusCode;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public int getStatusCode() { return statusCode; }

    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Map<String, String> getErrors() { return errors; }

    public void setErrors(Map<String, String> errors) { this.errors = errors; }
}
