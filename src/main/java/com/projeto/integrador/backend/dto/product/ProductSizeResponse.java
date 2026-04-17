package com.projeto.integrador.backend.dto.product;

import java.util.UUID;

public record ProductSizeResponse(UUID id, String size, int stockQuantity) {}
