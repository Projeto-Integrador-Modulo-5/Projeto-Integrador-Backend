package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.enums.OrderStatus;
import com.projeto.integrador.backend.dto.dashboard.DashboardResponse;
import com.projeto.integrador.backend.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private OrderRepository orderRepository;

    @InjectMocks private DashboardService dashboardService;

    @Test
    void getDashboard_shouldReturnAggregatedOrderCounts() {
        when(orderRepository.count()).thenReturn(100L);
        when(orderRepository.countByStatus(OrderStatus.PROCESSING)).thenReturn(40L);
        when(orderRepository.countByStatus(OrderStatus.SHIPPED)).thenReturn(35L);
        when(orderRepository.countByStatus(OrderStatus.DELIVERED)).thenReturn(25L);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.total()).isEqualTo(100L);
        assertThat(result.processing()).isEqualTo(40L);
        assertThat(result.shipped()).isEqualTo(35L);
        assertThat(result.delivered()).isEqualTo(25L);
    }

    @Test
    void getDashboard_shouldReturnZerosWhenNoOrders() {
        when(orderRepository.count()).thenReturn(0L);
        when(orderRepository.countByStatus(OrderStatus.PROCESSING)).thenReturn(0L);
        when(orderRepository.countByStatus(OrderStatus.SHIPPED)).thenReturn(0L);
        when(orderRepository.countByStatus(OrderStatus.DELIVERED)).thenReturn(0L);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.total()).isZero();
        assertThat(result.processing()).isZero();
        assertThat(result.shipped()).isZero();
        assertThat(result.delivered()).isZero();
    }
}
