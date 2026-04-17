package com.projeto.integrador.backend.dto.auth;

/**
 * Corpo opcional do POST /auth/logout.
 * refreshToken pode ser nulo caso o cliente não o possua.
 */
public record LogoutRequest(String refreshToken) {}
