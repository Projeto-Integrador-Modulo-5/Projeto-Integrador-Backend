package com.projeto.integrador.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
    @NotBlank String currentPassword,
    @NotBlank @Size(min = 8, message = "Nova senha deve ter no mínimo 8 caracteres") String newPassword
) {}
