package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.Notification;
import com.projeto.integrador.backend.domain.entity.Order;
import com.projeto.integrador.backend.domain.enums.NotificationType;
import com.projeto.integrador.backend.domain.enums.OrderStatus;
import com.projeto.integrador.backend.dto.order.OrderResponse;
import com.projeto.integrador.backend.dto.order.UpdateOrderStatusRequest;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.messaging.NotificationEvent;
import com.projeto.integrador.backend.messaging.OrderEventProducer;
import com.projeto.integrador.backend.repository.NotificationRepository;
import com.projeto.integrador.backend.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderAdminService {

    private static final Logger log = LoggerFactory.getLogger(OrderAdminService.class);

    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final OrderEventProducer orderEventProducer;
    private final OrderService orderService;

    public OrderAdminService(OrderRepository orderRepository,
                              NotificationRepository notificationRepository,
                              OrderEventProducer orderEventProducer,
                              OrderService orderService) {
        this.orderRepository        = orderRepository;
        this.notificationRepository = notificationRepository;
        this.orderEventProducer     = orderEventProducer;
        this.orderService           = orderService;
    }

    public Page<OrderResponse> getAllOrders(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = (status != null)
            ? orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
            : orderRepository.findAllByOrderByCreatedAtDesc(pageable);
        return orders.map(orderService::toResponse);
    }

    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + orderId));
        return orderService.toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + orderId));

        order.setStatus(request.status());
        order = orderRepository.save(order);

        String statusName = request.status().name();
        String message    = buildMessage(statusName, orderId.toString());

        // 1. Persiste no BD — garante que o usuário veja mesmo se estiver offline
        try {
            NotificationType type = NotificationType.valueOf("ORDER_" + statusName);
            Notification notification = new Notification();
            notification.setUser(order.getUser());
            notification.setOrder(order);
            notification.setType(type);
            notification.setMessage(message);
            notificationRepository.save(notification);
        } catch (IllegalArgumentException e) {
            log.warn("NotificationType não mapeado para status {}, pulando persistência", statusName);
        }

        // 2. Publica no Kafka — entrega em tempo real via WebSocket se o usuário estiver online
        try {
            orderEventProducer.publishNotification(new NotificationEvent(
                order.getUser().getId(), orderId, "ORDER_" + statusName, message
            ));
        } catch (Exception e) {
            log.warn("Falha ao publicar notificação Kafka para pedido {}: {}", orderId, e.getMessage());
        }

        return orderService.toResponse(order);
    }

    private String buildMessage(String status, String orderId) {
        String shortId = orderId.substring(0, 8).toUpperCase();
        return switch (status) {
            case "SHIPPED"   -> "Seu pedido #" + shortId + " foi enviado!";
            case "DELIVERED" -> "Seu pedido #" + shortId + " foi entregue!";
            case "CANCELLED" -> "Seu pedido #" + shortId + " foi cancelado.";
            default          -> "Seu pedido #" + shortId + " está sendo processado.";
        };
    }
}
