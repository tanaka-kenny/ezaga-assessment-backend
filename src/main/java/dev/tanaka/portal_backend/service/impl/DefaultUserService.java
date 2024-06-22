package dev.tanaka.portal_backend.service.impl;

import dev.tanaka.portal_backend.domain.User;
import dev.tanaka.portal_backend.dto.UserDto;
import dev.tanaka.portal_backend.exception.UserNotFoundException;
import dev.tanaka.portal_backend.repository.UserRepository;
import dev.tanaka.portal_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto findUserByEmail(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);

        if (byEmail.isEmpty()) throw new UserNotFoundException(email);

        User user = byEmail.get();
        return new UserDto(user.getFirstname(), user.getLastname(), user.getEmail());
    }

    @Override
    public UserDto updateUser(UserDto user) {
        Optional<User> byEmail = userRepository.findByEmail(user.email());

        if (byEmail.isEmpty()) throw new UserNotFoundException(user.email());

        User savedUser = byEmail.get();
        savedUser.setFirstname(user.firstname());
        savedUser.setLastname(user.lastname());

        userRepository.save(savedUser);
        return new UserDto(savedUser.getFirstname(), savedUser.getLastname(), savedUser.getEmail());
    }
}
