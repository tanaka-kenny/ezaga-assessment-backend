package dev.tanaka.portal_backend.service.impl;

import dev.tanaka.portal_backend.domain.User;
import dev.tanaka.portal_backend.dto.UserDto;
import dev.tanaka.portal_backend.exception.UserNotFoundException;
import dev.tanaka.portal_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultUserService userService;

    private User mockUser;
    private UserDto mockUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setFirstname("John");
        mockUser.setLastname("Doe");
        mockUser.setEmail("john.doe@example.com");

        mockUserDto = new UserDto("John", "Doe", "john.doe@example.com");
    }

    @Test
    void findUserByEmail_UserExists_ReturnsUserDto() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(mockUser));

        UserDto result = userService.findUserByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals("John", result.firstname());
        assertEquals("Doe", result.lastname());
        assertEquals("john.doe@example.com", result.email());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void findUserByEmail_UserDoesNotExist_ThrowsUserNotFoundException() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserByEmail("john.doe@example.com"));
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void updateUser_UserExists_ReturnsUpdatedUserDto() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserDto updatedUserDto = new UserDto("Jane", "Doe", "john.doe@example.com");
        UserDto result = userService.updateUser(updatedUserDto);

        assertNotNull(result);
        assertEquals("Jane", result.firstname());
        assertEquals("Doe", result.lastname());
        assertEquals("john.doe@example.com", result.email());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_UserDoesNotExist_ThrowsUserNotFoundException() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        UserDto updatedUserDto = new UserDto("Jane", "Doe", "john.doe@example.com");
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(updatedUserDto));
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
}
