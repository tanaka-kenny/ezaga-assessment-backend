package dev.tanaka.portal_backend.dto;

import dev.tanaka.portal_backend.enumeration.Role;

public record RegisterRequest(String firstname,
         String lastname,
         String email,
         String password,
        Role role) {
}
