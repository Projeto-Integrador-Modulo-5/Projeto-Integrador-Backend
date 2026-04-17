package com.projeto.integrador.backend.controller;

import com.projeto.integrador.backend.domain.enums.OrderStatus;
import com.projeto.integrador.backend.dto.dashboard.DashboardResponse;
import com.projeto.integrador.backend.dto.order.OrderResponse;
import com.projeto.integrador.backend.dto.order.UpdateOrderStatusRequest;
import com.projeto.integrador.backend.service.DashboardService;
import com.projeto.integrador.backend.service.OrderAdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderAdminService orderAdminService;
    private final DashboardService dashboardService;

    public AdminOrderController(OrderAdminService orderAdminService, DashboardService dashboardService) {
        this.orderAdminService = orderAdminService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> listOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(orderAdminService.getAllOrders(status, page, size));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderAdminService.getOrderById(id));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable UUID id,
                                                       @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderAdminService.updateOrderStatus(id, request));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}
