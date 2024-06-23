package dev.tanaka.portal_backend.service.impl;

import dev.tanaka.portal_backend.domain.ConfirmationCode;
import dev.tanaka.portal_backend.domain.Token;
import dev.tanaka.portal_backend.domain.User;
import dev.tanaka.portal_backend.dto.AuthRequest;
import dev.tanaka.portal_backend.dto.AuthResponse;
import dev.tanaka.portal_backend.dto.RegisterRequest;
import dev.tanaka.portal_backend.enumeration.TokenType;
import dev.tanaka.portal_backend.exception.ExistingEmailFoundException;
import dev.tanaka.portal_backend.exception.UserNotFoundException;
import dev.tanaka.portal_backend.repository.ConfirmationCodeRepository;
import dev.tanaka.portal_backend.repository.TokenRepository;
import dev.tanaka.portal_backend.repository.UserRepository;
import dev.tanaka.portal_backend.service.AuthService;
import dev.tanaka.portal_backend.service.EmailService;
import dev.tanaka.portal_backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public AuthResponse login(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.email(),
                        authRequest.password()
                )
        );
        var user = userRepository.findByEmail(authRequest.email())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return new AuthResponse(jwtToken, refreshToken, authRequest.email());
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.email());
        if (optionalUser.isPresent()) throw new ExistingEmailFoundException(request.email());

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
        return new AuthResponse(jwtToken, refreshToken, request.email());
    }

    @Override
    public void logout(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) throw new UserNotFoundException(email);

        User user = optionalUser.get();
        revokeAllUserTokens(user);
    }

    @Override
    public void requestConfirmationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) throw new UserNotFoundException(email);

        User user = optionalUser.get();
        Random random = new Random();
        int code = random.nextInt(1100, 9999);
        ConfirmationCode confirmationCode = new ConfirmationCode();
        confirmationCode.setConfirmationCode(code);
        confirmationCode.setUser(user);

        ConfirmationCode existingConfirmationCode = confirmationCodeRepository.findByUserId(user.getId());
        if (Objects.nonNull(existingConfirmationCode)) {
            confirmationCodeRepository.delete(existingConfirmationCode);
        }
        this.confirmationCodeRepository.save(confirmationCode);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Password Reset");
        simpleMailMessage.setText("User this code to reset your password. " + code);
        this.emailService.sendEmail(simpleMailMessage);
    }

    @Override
    public Boolean verifyConfirmationCode(Integer code, AuthRequest authRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(authRequest.email());
        if (optionalUser.isEmpty()) throw new UserNotFoundException(authRequest.email());
        User user = optionalUser.get();

        ConfirmationCode confirmationCode = confirmationCodeRepository.findByUserId(user.getId());

        if (Objects.isNull(confirmationCode)) {
            return false;
        }

        if (confirmationCode.getConfirmationCode().equals(code)) {
            user.setPassword(passwordEncoder.encode(authRequest.password()));
            userRepository.save(user);
            return true;
        }

        return false;
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


    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
