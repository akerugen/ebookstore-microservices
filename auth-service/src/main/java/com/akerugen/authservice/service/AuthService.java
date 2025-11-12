package com.akerugen.authservice.service;

import com.akerugen.authservice.dto.request.LoginRequest;
import com.akerugen.authservice.dto.request.RegisterRequest;
import com.akerugen.authservice.dto.request.RefreshTokenRequest;
import com.akerugen.authservice.dto.response.AuthResponse;
import com.akerugen.authservice.dto.response.ValidationResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    ValidationResponse validateToken(String token);

    void logout(String refreshToken);
}