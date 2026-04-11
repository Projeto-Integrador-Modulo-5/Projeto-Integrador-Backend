package com.projeto.integrador.backend.dto.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
    UUID productId,
    String productName,
    String size,
    int quantity,
    BigDecimal unitPrice
) {}
