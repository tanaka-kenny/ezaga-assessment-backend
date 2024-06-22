package dev.tanaka.portal_backend.service;

import dev.tanaka.portal_backend.dto.AuthRequest;
import dev.tanaka.portal_backend.dto.AuthResponse;
import dev.tanaka.portal_backend.dto.RegisterRequest;

public interface AuthService {

    AuthResponse login(AuthRequest authRequest);
    AuthResponse register(RegisterRequest request);
    void logout(String email);
}
