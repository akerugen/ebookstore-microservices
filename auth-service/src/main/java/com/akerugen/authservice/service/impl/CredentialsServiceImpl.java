package com.akerugen.authservice.service.impl;

import com.akerugen.authservice.dto.request.RegisterRequest;
import com.akerugen.authservice.entity.Credentials;
import com.akerugen.authservice.exception.AuthenticationException;
import com.akerugen.authservice.repo.CredentialsRepository;
import com.akerugen.authservice.service.CredentialsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CredentialsServiceImpl implements CredentialsService {

    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;

    public CredentialsServiceImpl(CredentialsRepository credentialsRepository,
                                  PasswordEncoder passwordEncoder) {
        this.credentialsRepository = credentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // хэширует пароль и сохраняет
    @Override
    public Credentials createCredentials(RegisterRequest request, Long userId) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        Credentials credentials = new Credentials(
                userId,
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                LocalDateTime.now()
        );

        return credentialsRepository.save(credentials);
    }

    @Override
    public Credentials findByUsername(String username) {
        return credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }

    @Override
    public Credentials findByEmail(String email) {
        return credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }

    @Override
    public Credentials findByUsernameOrEmail(String username, String email) {
        return credentialsRepository.findByUsernameOrEmail(username, email)
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }

    @Override
    public boolean existsByUsername(String username) {
        return credentialsRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public void deactivateCredentials(Long userId) {
        Credentials credentials = credentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthenticationException("Credentials not found"));
        credentials.setIsActive(false);
        credentials.setUpdatedAt(LocalDateTime.now());
        credentialsRepository.save(credentials);
    }
}
