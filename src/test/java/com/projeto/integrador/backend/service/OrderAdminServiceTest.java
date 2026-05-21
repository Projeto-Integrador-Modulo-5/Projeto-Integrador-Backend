package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.Order;
import com.projeto.integrador.backend.domain.entity.User;
import com.projeto.integrador.backend.domain.enums.OrderStatus;
import com.projeto.integrador.backend.domain.enums.Role;
import com.projeto.integrador.backend.dto.order.OrderResponse;
import com.projeto.integrador.backend.dto.order.UpdateOrderStatusRequest;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.messaging.NotificationEvent;
import com.projeto.integrador.backend.messaging.OrderEventProducer;
import com.projeto.integrador.backend.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderAdminServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderEventProducer orderEventProducer;
    @Mock private OrderService orderService;

    @InjectMocks private OrderAdminService orderAdminService;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User("Admin", "admin@test.com", "hash", Role.ADMIN);
        order = buildOrder(OrderStatus.PROCESSING);
    }

    // ── getAllOrders ───────────────────────────────────────────────────────────

    @Test
    void getAllOrders_shouldReturnAllOrdersWhenNoStatusFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);
        when(orderRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(page);
        when(orderService.toResponse(order)).thenReturn(buildOrderResponse(order));

        Page<OrderResponse> result = orderAdminService.getAllOrders(null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findAllByOrderByCreatedAtDesc(any(Pageable.class));
    }

    @Test
    void getAllOrders_shouldFilterByStatusWhenProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);
        when(orderRepository.findByStatusOrderByCreatedAtDesc(eq(OrderStatus.PROCESSING), any(Pageable.class)))
            .thenReturn(page);
        when(orderService.toResponse(order)).thenReturn(buildOrderResponse(order));

        Page<OrderResponse> result = orderAdminService.getAllOrders(OrderStatus.PROCESSING, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findByStatusOrderByCreatedAtDesc(eq(OrderStatus.PROCESSING), any(Pageable.class));
    }

    // ── getOrderById ──────────────────────────────────────────────────────────

    @Test
    void getOrderById_shouldReturnOrderResponse() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderService.toResponse(order)).thenReturn(buildOrderResponse(order));

        OrderResponse result = orderAdminService.getOrderById(orderId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("PROCESSING");
    }

    @Test
    void getOrderById_shouldThrowWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderAdminService.getOrderById(orderId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Pedido não encontrado");
    }

    // ── updateOrderStatus ─────────────────────────────────────────────────────

    @Test
    void updateOrderStatus_shouldUpdateStatusAndPublishNotification() {
        UUID orderId = UUID.randomUUID();
        Order orderWithUser = buildOrderWithUser(OrderStatus.PROCESSING);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderWithUser));
        when(orderRepository.save(orderWithUser)).thenReturn(orderWithUser);
        when(orderService.toResponse(orderWithUser)).thenReturn(
            new OrderResponse(orderId, "SHIPPED", BigDecimal.valueOf(99.90),
                "BR123456789PT", null, null, List.of(),
                user.getName(), user.getEmail())
        );

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.SHIPPED);
        OrderResponse result = orderAdminService.updateOrderStatus(orderId, request);

        assertThat(result.status()).isEqualTo("SHIPPED");
        verify(orderRepository).save(orderWithUser);
        verify(orderEventProducer).publishNotification(any(NotificationEvent.class));
    }

    @Test
    void updateOrderStatus_shouldBuildCorrectMessageForDelivered() {
        UUID orderId = UUID.randomUUID();
        Order orderWithUser = buildOrderWithUser(OrderStatus.SHIPPED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderWithUser));
        when(orderRepository.save(orderWithUser)).thenReturn(orderWithUser);
        when(orderService.toResponse(orderWithUser)).thenReturn(buildOrderResponse(orderWithUser));

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.DELIVERED);
        orderAdminService.updateOrderStatus(orderId, request);

        verify(orderEventProducer).publishNotification(argThat(event ->
            event.message().contains("entregue")));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Order buildOrder(OrderStatus status) {
        Order o = new Order();
        o.setStatus(status);
        o.setTotal(BigDecimal.valueOf(99.90));
        return o;
    }

    private Order buildOrderWithUser(OrderStatus status) {
        Order o = buildOrder(status);
        o.setUser(user);
        return o;
    }

    private OrderResponse buildOrderResponse(Order o) {
        String name  = o.getUser() != null ? o.getUser().getName()  : null;
        String email = o.getUser() != null ? o.getUser().getEmail() : null;
        return new OrderResponse(
            o.getId() != null ? o.getId() : UUID.randomUUID(),
            o.getStatus().name(),
            o.getTotal(),
            o.getTrackingCode(),
            o.getCreatedAt(),
            o.getUpdatedAt(),
            List.of(),
            name,
            email
        );
    }
}
