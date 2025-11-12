package com.akerugen.authservice.feign.dto;

public class UserResponseDto {
    public Long id;
    public String username;
    public String email;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}
