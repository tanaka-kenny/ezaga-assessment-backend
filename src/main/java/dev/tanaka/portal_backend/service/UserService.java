package dev.tanaka.portal_backend.service;

import dev.tanaka.portal_backend.dto.UserDto;

public interface UserService {

    public UserDto findUserByEmail(String email);
    public UserDto updateUser(UserDto user);
}
