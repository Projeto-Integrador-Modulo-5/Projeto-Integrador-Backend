package com.projeto.integrador.backend.dto.cart;

import com.projeto.integrador.backend.domain.enums.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequest(
    @NotNull Size size,
    @Min(1) int quantity
) {}
