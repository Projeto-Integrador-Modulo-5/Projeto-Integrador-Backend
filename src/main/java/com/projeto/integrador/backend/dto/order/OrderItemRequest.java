package com.projeto.integrador.backend.dto.order;

import com.projeto.integrador.backend.domain.enums.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record OrderItemRequest(
    @NotNull UUID productId,
    @NotNull Size size,
    @Min(1) int quantity
) {}
