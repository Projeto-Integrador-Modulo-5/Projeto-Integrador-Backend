package com.projeto.integrador.backend.service;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemData(UUID productId, String productName, String size, int quantity, BigDecimal unitPrice) {}
