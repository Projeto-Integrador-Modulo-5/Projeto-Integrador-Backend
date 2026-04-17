package com.projeto.integrador.backend.dto.auth;

import java.util.UUID;

public record AuthResponse(
    String token,
    String refreshToken,
    String type,
    UUID userId,
    String email,
    String name,
    String role
) {
    /** Construtor completo sem precisar informar type. */
    public AuthResponse(String token, String refreshToken, UUID userId, String email, String name, String role) {
        this(token, refreshToken, "Bearer", userId, email, name, role);
    }
}
