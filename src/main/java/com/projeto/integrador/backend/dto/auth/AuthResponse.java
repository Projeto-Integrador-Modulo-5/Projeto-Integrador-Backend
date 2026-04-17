package com.projeto.integrador.backend.dto.auth;

import java.util.UUID;

public record AuthResponse(
    String token,
    String type,
    UUID userId,
    String email,
    String name,
    String role
) {
    public AuthResponse(String token, UUID userId, String email, String name, String role) {
        this(token, "Bearer", userId, email, name, role);
    }
}
