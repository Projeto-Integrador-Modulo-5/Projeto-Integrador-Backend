package com.projeto.integrador.backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
    @NotBlank(message = "Nome é obrigatório") String name,
    @NotBlank @Email(message = "Email inválido") String email
) {}
