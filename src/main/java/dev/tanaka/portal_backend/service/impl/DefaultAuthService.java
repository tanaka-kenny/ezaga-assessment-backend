package dev.tanaka.portal_backend.service.impl;

import dev.tanaka.portal_backend.domain.Token;
import dev.tanaka.portal_backend.domain.User;
import dev.tanaka.portal_backend.dto.AuthRequest;
import dev.tanaka.portal_backend.dto.AuthResponse;
import dev.tanaka.portal_backend.dto.RegisterRequest;
import dev.tanaka.portal_backend.enumeration.TokenType;
import dev.tanaka.portal_backend.repository.TokenRepository;
import dev.tanaka.portal_backend.repository.UserRepository;
import dev.tanaka.portal_backend.service.AuthService;
import dev.tanaka.portal_backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        return null;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        var user = User.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return new AuthResponse(jwtToken, refreshToken);
    }


    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
