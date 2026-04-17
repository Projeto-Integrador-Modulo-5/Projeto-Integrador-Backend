package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.*;
import com.projeto.integrador.backend.domain.enums.NotificationType;
import com.projeto.integrador.backend.dto.order.*;
import com.projeto.integrador.backend.exception.BusinessException;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.messaging.NotificationEvent;
import com.projeto.integrador.backend.messaging.OrderCreatedEvent;
import com.projeto.integrador.backend.messaging.OrderEventProducer;
import com.projeto.integrador.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final NotificationRepository notificationRepository;
    private final CartService cartService;
    private final OrderEventProducer orderEventProducer;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        AddressRepository addressRepository, ProductRepository productRepository,
                        NotificationRepository notificationRepository, CartService cartService,
                        OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.notificationRepository = notificationRepository;
        this.cartService = cartService;
        this.orderEventProducer = orderEventProducer;
    }

    @Transactional
    public OrderResponse createOrder(String email, CreateOrderRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Address address = addressRepository.findByIdAndUserId(request.addressId(), user.getId())
            .orElseThrow(() -> new BusinessException("Endereço inválido ou não pertence ao usuário"));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + itemReq.productId()));

            OrderItem item = new OrderItem(order, product, itemReq.size(), itemReq.quantity(), product.getPrice());
            order.getItems().add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity())));
        }

        order.setTotal(total);
        order = orderRepository.save(order);

        // Limpa o carrinho
        cartService.clearCart(user.getId());

        // Persiste notificação de criação
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setOrder(order);
        notification.setType(NotificationType.ORDER_CREATED);
        notification.setMessage("Seu pedido #" + order.getId().toString().substring(0, 8).toUpperCase() + " foi criado e está sendo processado!");
        notificationRepository.save(notification);

        // Publica no Kafka
        List<OrderCreatedEvent.OrderItemEvent> itemEvents = request.items().stream()
            .map(i -> new OrderCreatedEvent.OrderItemEvent(i.productId(), i.size().name(), i.quantity()))
            .toList();

        orderEventProducer.publishOrderCreated(new OrderCreatedEvent(
            order.getId(), user.getId(), itemEvents, address.getId(), LocalDateTime.now()
        ));

        return toResponse(order);
    }

    public List<OrderResponse> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
            .map(this::toResponse)
            .toList();
    }

    public OrderResponse getUserOrderById(String email, UUID orderId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
        return toResponse(order);
    }

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getSize().name(),
                item.getQuantity(),
                item.getUnitPrice()
            ))
            .toList();
        return new OrderResponse(order.getId(), order.getStatus().name(), order.getTotal(),
            order.getTrackingCode(), order.getCreatedAt(), order.getUpdatedAt(), items);
    }
}
