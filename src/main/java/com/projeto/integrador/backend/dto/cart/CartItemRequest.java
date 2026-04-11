package com.projeto.integrador.backend.dto.cart;

import com.projeto.integrador.backend.domain.enums.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CartItemRequest(
    @NotNull UUID productId,
    @NotNull Size size,
    @Min(1) int quantity
) {}
