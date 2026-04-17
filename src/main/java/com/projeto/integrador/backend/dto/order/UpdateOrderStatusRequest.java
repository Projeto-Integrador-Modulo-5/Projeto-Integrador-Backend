package com.projeto.integrador.backend.dto.order;

import com.projeto.integrador.backend.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
