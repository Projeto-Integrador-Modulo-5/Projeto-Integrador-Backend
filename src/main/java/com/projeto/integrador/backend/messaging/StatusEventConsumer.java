package com.projeto.integrador.backend.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.integrador.backend.domain.entity.Notification;
import com.projeto.integrador.backend.domain.entity.Order;
import com.projeto.integrador.backend.domain.enums.NotificationType;
import com.projeto.integrador.backend.domain.enums.OrderStatus;
import com.projeto.integrador.backend.repository.NotificationRepository;
import com.projeto.integrador.backend.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StatusEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(StatusEventConsumer.class);

    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    public StatusEventConsumer(OrderRepository orderRepository,
                                NotificationRepository notificationRepository,
                                ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order.status.updated", groupId = "backend-group")
    @Transactional
    public void consume(String message) {
        try {
            OrderStatusUpdatedEvent event = objectMapper.readValue(message, OrderStatusUpdatedEvent.class);
            log.info("Recebido order.status.updated: orderId={}, status={}", event.orderId(), event.newStatus());

            Order order = orderRepository.findById(event.orderId()).orElse(null);
            if (order == null) {
                log.warn("Pedido não encontrado: {}", event.orderId());
                return;
            }

            order.setStatus(OrderStatus.valueOf(event.newStatus()));
            if (event.trackingCode() != null) {
                order.setTrackingCode(event.trackingCode());
            }
            orderRepository.save(order);

            // Persiste notificação
            Notification notification = new Notification();
            notification.setUser(order.getUser());
            notification.setOrder(order);
            notification.setType(NotificationType.valueOf("ORDER_" + event.newStatus()));
            notification.setMessage(buildMessage(event.newStatus(), order.getId().toString()));
            notificationRepository.save(notification);

            log.info("Pedido {} atualizado para {}", event.orderId(), event.newStatus());
        } catch (Exception e) {
            log.error("Erro ao processar order.status.updated: {}", e.getMessage(), e);
        }
    }

    private String buildMessage(String status, String orderId) {
        return switch (status) {
            case "SHIPPED" -> "Seu pedido #" + orderId.substring(0, 8).toUpperCase() + " foi enviado!";
            case "DELIVERED" -> "Seu pedido #" + orderId.substring(0, 8).toUpperCase() + " foi entregue!";
            default -> "Status do seu pedido atualizado para " + status;
        };
    }
}
