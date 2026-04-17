package com.projeto.integrador.backend.dto.cart;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
    UUID productId,
    String productName,
    String size,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal
) {}
