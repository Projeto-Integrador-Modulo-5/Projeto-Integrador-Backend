package com.projeto.integrador.backend.messaging;

import java.util.UUID;

public record NotificationEvent(UUID userId, UUID orderId, String type, String message) {}
