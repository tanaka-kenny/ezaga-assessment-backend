package dev.tanaka.portal_backend.controller;

import dev.tanaka.portal_backend.dto.UserDto;
import dev.tanaka.portal_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{email}")
    public UserDto getUser(@PathVariable String email) {
        log.info("Get user with email {}", email);
        return userService.findUserByEmail(email);
    }

    @PutMapping("/{email}")
    public UserDto updateUser(@PathVariable String email, @RequestBody UserDto userDto) {
        log.info("Updating user with email: {}", email);
        return userService.updateUser(userDto);
    }
}
