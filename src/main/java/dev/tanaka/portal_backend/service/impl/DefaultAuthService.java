package dev.tanaka.portal_backend.service.impl;

import dev.tanaka.portal_backend.dto.AuthRequest;
import dev.tanaka.portal_backend.dto.AuthResponse;
import dev.tanaka.portal_backend.dto.RegisterRequest;
import dev.tanaka.portal_backend.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class DefaultAuthService implements AuthService {
    @Override
    public AuthResponse login(AuthRequest authRequest) {
        return null;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        return null;
    }
}
