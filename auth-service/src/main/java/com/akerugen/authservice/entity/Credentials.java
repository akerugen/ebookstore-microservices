package com.akerugen.authservice.entity;

import com.akerugen.authservice.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity для хранения учётных данных (аутентификация)
 * НЕ СОДЕРЖИТ: userId (больше не нужен, используем username как бизнес-ключ)
 */
@Entity
@Table(name = "credentials")
public class Credentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Роль пользователя в системе
     * По умолчанию USER при регистрации
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Credentials() {
    }

    /**
     * Конструтор для создания учетки с ролью USER
     */
    public Credentials(String username, String email, String password, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
        this.createdAt = createdAt;
        this.isActive = true;
        this.failedLoginAttempts = 0;
    }

    /**
     * Конструтор для создания учетки с ролью SUPER_USER
     */
    public Credentials(String username, String email, String password, Role role, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.isActive = true;
        this.failedLoginAttempts = 0;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }

    public Boolean getIsActive() { return isActive; }

    public void setIsActive(Boolean active) { isActive = active; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getLastLogin() { return lastLogin; }

    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
