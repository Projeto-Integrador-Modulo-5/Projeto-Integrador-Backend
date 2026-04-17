package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.enums.OrderStatus;
import com.projeto.integrador.backend.dto.dashboard.DashboardResponse;
import com.projeto.integrador.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;

    public DashboardService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public DashboardResponse getDashboard() {
        long total = orderRepository.count();
        long processing = orderRepository.countByStatus(OrderStatus.PROCESSING);
        long shipped = orderRepository.countByStatus(OrderStatus.SHIPPED);
        long delivered = orderRepository.countByStatus(OrderStatus.DELIVERED);
        return new DashboardResponse(total, processing, shipped, delivered);
    }
}
