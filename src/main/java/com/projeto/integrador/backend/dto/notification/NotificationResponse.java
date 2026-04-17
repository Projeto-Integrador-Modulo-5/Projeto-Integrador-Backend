package com.projeto.integrador.backend.dto.notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
    UUID id,
    UUID orderId,
    String type,
    String message,
    boolean read,
    LocalDateTime sentAt
) {}
