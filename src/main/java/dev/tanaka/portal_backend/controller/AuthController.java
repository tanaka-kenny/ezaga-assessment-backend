package dev.tanaka.portal_backend.controller;

import dev.tanaka.portal_backend.dto.AuthRequest;
import dev.tanaka.portal_backend.dto.AuthResponse;
import dev.tanaka.portal_backend.dto.RegisterRequest;
import dev.tanaka.portal_backend.service.AuthService;
import dev.tanaka.portal_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse authenticate(@RequestBody AuthRequest request) {
        log.info("Authenticating request: {}", request);
        AuthResponse authResponse = authService.login(request);
        log.info("Auth response: {}", authResponse);
        return authResponse;
    }

    @PostMapping("/logout/{email}")
    public void logout(@PathVariable String email) {
        authService.logout(email);
    }

    @PostMapping("forgot-password/{email}")
    public void forgotPassword(@PathVariable String email) {
        authService.forgotPassword(email);
    }
}
