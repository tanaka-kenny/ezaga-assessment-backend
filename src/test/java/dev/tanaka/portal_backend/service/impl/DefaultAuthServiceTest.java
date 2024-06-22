package dev.tanaka.portal_backend.service.impl;

import dev.tanaka.portal_backend.domain.Token;
import dev.tanaka.portal_backend.domain.User;
import dev.tanaka.portal_backend.dto.AuthRequest;
import dev.tanaka.portal_backend.dto.AuthResponse;
import dev.tanaka.portal_backend.dto.RegisterRequest;
import dev.tanaka.portal_backend.enumeration.Role;
import dev.tanaka.portal_backend.enumeration.TokenType;
import dev.tanaka.portal_backend.exception.ExistingEmailFoundException;
import dev.tanaka.portal_backend.exception.UserNotFoundException;
import dev.tanaka.portal_backend.repository.TokenRepository;
import dev.tanaka.portal_backend.repository.UserRepository;
import dev.tanaka.portal_backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private DefaultAuthService authService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ValidAuthRequest_ReturnsAuthResponse() {
        String email = "test@example.com";
        String password = "password";
        AuthRequest authRequest = new AuthRequest(email, password);
        User user = new User(1, "John", "Doe", email, passwordEncoder.encode(password), Role.USER, new ArrayList<>());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mockJwtToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("mockRefreshToken");

        AuthResponse response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.accessToken());
        assertEquals("mockRefreshToken", response.refreshToken());
        assertEquals(email, response.email());
        verify(userRepository, times(1)).findByEmail(email);
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsExistingEmailFoundException() {
        RegisterRequest registerRequest = new RegisterRequest("John", "Doe", "existing@example.com", "password", Role.USER);
        User existingUser = new User(null, "Existing", "User", "existing@example.com", passwordEncoder.encode("password"), Role.USER, new ArrayList<>());

        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.of(existingUser));

        assertThrows(ExistingEmailFoundException.class, () -> authService.register(registerRequest));
        verify(userRepository, times(1)).findByEmail(registerRequest.email());
        verifyNoMoreInteractions(userRepository, jwtService, tokenRepository);
    }

    @Test
    void logout_ExistingUser_RevokesUserTokens() {
        String email = "test@example.com";
        User user = new User(1, "John", "Doe", email, passwordEncoder.encode("password"), Role.USER, new ArrayList<>());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(Collections.singletonList(
                new Token(1, "mockToken", TokenType.BEARER, false, false, user)
        ));

        authService.logout(email);

        verify(userRepository, times(1)).findByEmail(email);
        verify(tokenRepository, times(1)).findAllValidTokenByUser(user.getId());
        verify(tokenRepository, times(1)).saveAll(anyList());
    }

    @Test
    void logout_NonexistentUser_ThrowsUserNotFoundException() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.logout(email));
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(tokenRepository);
    }
}
