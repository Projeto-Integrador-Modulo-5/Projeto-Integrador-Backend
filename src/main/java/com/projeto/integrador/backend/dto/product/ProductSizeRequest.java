package com.projeto.integrador.backend.dto.product;

import com.projeto.integrador.backend.domain.enums.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductSizeRequest(
    @NotNull Size size,
    @Min(0) int stockQuantity
) {}
