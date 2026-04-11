package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.Address;
import com.projeto.integrador.backend.domain.entity.Order;
import com.projeto.integrador.backend.domain.entity.User;
import com.projeto.integrador.backend.domain.enums.Role;
import com.projeto.integrador.backend.dto.order.OrderResponse;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.messaging.OrderEventProducer;
import com.projeto.integrador.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private ProductRepository productRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private CartService cartService;
    @Mock private OrderEventProducer orderEventProducer;

    @InjectMocks private OrderService orderService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Test", "test@test.com", "pass", Role.CUSTOMER);
    }

    @Test
    void getUserOrders_shouldReturnEmptyListWhenNoOrders() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())).thenReturn(List.of());

        List<OrderResponse> result = orderService.getUserOrders("test@test.com");

        assertThat(result).isEmpty();
    }

    @Test
    void getUserOrders_shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("naoexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getUserOrders("naoexiste@test.com"))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
