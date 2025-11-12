package com.akerugen.authservice.service;

import com.akerugen.authservice.dto.request.RegisterRequest;
import com.akerugen.authservice.entity.Credentials;

public interface CredentialsService {

    Credentials createCredentials(RegisterRequest request, Long userId);

    Credentials findByUsername(String username);

    Credentials findByEmail(String email);

    Credentials findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    void deactivateCredentials(Long userId);
}
