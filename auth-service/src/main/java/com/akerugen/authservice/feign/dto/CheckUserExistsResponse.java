package com.akerugen.authservice.feign.dto;

/**
 * DTO ответ при проверке существования пользователя
 * Приходит от user-service endpoint GET /api/users/check
 */
public class CheckUserExistsResponse {

    private boolean exists;
    private String message;


    public CheckUserExistsResponse() {
    }

    public CheckUserExistsResponse(boolean exists, String message) {
        this.exists = exists;
        this.message = message;
    }

    public CheckUserExistsResponse(boolean exists) {
        this.exists = exists;
        this.message = exists ? "User already exists" : "User does not exist";
    }

    public boolean isExists() { return exists; }

    public void setExists(boolean exists) { this.exists = exists; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}