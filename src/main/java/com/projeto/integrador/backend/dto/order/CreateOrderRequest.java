package com.projeto.integrador.backend.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
    @NotNull UUID addressId,
    @NotEmpty List<OrderItemRequest> items
) {}
