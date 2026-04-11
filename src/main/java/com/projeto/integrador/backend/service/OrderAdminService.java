package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.Order;
import com.projeto.integrador.backend.domain.enums.OrderStatus;
import com.projeto.integrador.backend.dto.order.OrderResponse;
import com.projeto.integrador.backend.dto.order.UpdateOrderStatusRequest;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.messaging.NotificationEvent;
import com.projeto.integrador.backend.messaging.OrderEventProducer;
import com.projeto.integrador.backend.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderAdminService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final OrderService orderService;

    public OrderAdminService(OrderRepository orderRepository, OrderEventProducer orderEventProducer,
                              OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
        this.orderService = orderService;
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

        // Publica notificação via Kafka
        String message = buildMessage(request.status().name(), orderId.toString());
        orderEventProducer.publishNotification(new NotificationEvent(
            order.getUser().getId(), orderId, "ORDER_" + request.status().name(), message
        ));

        return orderService.toResponse(order);
    }

    private String buildMessage(String status, String orderId) {
        String shortId = orderId.substring(0, 8).toUpperCase();
        return switch (status) {
            case "SHIPPED" -> "Seu pedido #" + shortId + " foi enviado!";
            case "DELIVERED" -> "Seu pedido #" + shortId + " foi entregue!";
            default -> "Status do pedido #" + shortId + " atualizado.";
        };
    }
}
