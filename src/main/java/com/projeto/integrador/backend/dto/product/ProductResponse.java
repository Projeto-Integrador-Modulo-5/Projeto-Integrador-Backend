package com.projeto.integrador.backend.dto.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    String imageUrl,
    boolean active,
    List<ProductSizeResponse> sizes
) {}
