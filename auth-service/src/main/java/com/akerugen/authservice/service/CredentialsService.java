package com.akerugen.authservice.service;

import com.akerugen.authservice.dto.request.RegisterRequest;
import com.akerugen.authservice.entity.Credentials;

public interface CredentialsService {

    Credentials createCredentials(RegisterRequest request);
    Credentials findByUsername(String username);
    Credentials findByEmail(String email);
    Credentials findByUsernameOrEmail(String username, String email);
    Credentials findByUserId(Long userId);

    boolean existsByUsernameOrEmail(String username, String email);

    void deactivateCredentials(Long userId);
    void deactivateCredentials(String username);
    void changeUserRole(String username, String newRole);
    void incrementFailedLoginAttempts(String username);
    void resetFailedLoginAttempts(String username);
    void updateLastLogin(String username);
}
