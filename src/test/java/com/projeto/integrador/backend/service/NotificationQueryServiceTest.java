package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.Notification;
import com.projeto.integrador.backend.domain.entity.Order;
import com.projeto.integrador.backend.domain.entity.User;
import com.projeto.integrador.backend.domain.enums.NotificationType;
import com.projeto.integrador.backend.domain.enums.Role;
import com.projeto.integrador.backend.dto.notification.NotificationResponse;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.repository.NotificationRepository;
import com.projeto.integrador.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private NotificationQueryService notificationQueryService;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User("Victor", "victor@test.com", "hash", Role.CUSTOMER);
        order = new Order();
    }

    // ── getUserNotifications ──────────────────────────────────────────────────

    @Test
    void getUserNotifications_shouldReturnListOfNotifications() {
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));

        Notification n = buildNotification("Pedido criado!");
        when(notificationRepository.findByUserIdOrderBySentAtDesc(user.getId())).thenReturn(List.of(n));

        List<NotificationResponse> result = notificationQueryService.getUserNotifications("victor@test.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).message()).isEqualTo("Pedido criado!");
        assertThat(result.get(0).type()).isEqualTo("ORDER_CREATED");
        assertThat(result.get(0).read()).isFalse();
    }

    @Test
    void getUserNotifications_shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("naoexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationQueryService.getUserNotifications("naoexiste@test.com"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── markAsRead ────────────────────────────────────────────────────────────

    @Test
    void markAsRead_shouldSetReadTrue() {
        UUID notifId = UUID.randomUUID();
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        Notification n = buildNotification("Pedido enviado!");
        when(notificationRepository.findByIdAndUserId(notifId, user.getId())).thenReturn(Optional.of(n));
        when(notificationRepository.save(n)).thenReturn(n);

        notificationQueryService.markAsRead("victor@test.com", notifId);

        assertThat(n.isRead()).isTrue();
        verify(notificationRepository).save(n);
    }

    @Test
    void markAsRead_shouldThrowWhenNotificationNotFound() {
        UUID notifId = UUID.randomUUID();
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByIdAndUserId(notifId, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationQueryService.markAsRead("victor@test.com", notifId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Notificação não encontrada");
    }

    // ── markAllAsRead ─────────────────────────────────────────────────────────

    @Test
    void markAllAsRead_shouldDelegateToRepository() {
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));

        notificationQueryService.markAllAsRead("victor@test.com");

        verify(notificationRepository).markAllAsReadByUserId(user.getId());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Notification buildNotification(String message) {
        Notification n = new Notification();
        n.setUser(user);
        n.setOrder(order);
        n.setType(NotificationType.ORDER_CREATED);
        n.setMessage(message);
        n.setRead(false);
        return n;
    }
}
