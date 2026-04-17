package com.projeto.integrador.backend.controller;

import com.projeto.integrador.backend.dto.order.CreateOrderRequest;
import com.projeto.integrador.backend.dto.order.OrderResponse;
import com.projeto.integrador.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(201).body(orderService.createOrder(getCurrentEmail(), request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        return ResponseEntity.ok(orderService.getUserOrders(getCurrentEmail()));
    }

    @GetMapping("/my/{id}")
    public ResponseEntity<OrderResponse> getMyOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getUserOrderById(getCurrentEmail(), id));
    }
}
