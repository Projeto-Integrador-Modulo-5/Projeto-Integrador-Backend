package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.Notification;
import com.projeto.integrador.backend.dto.notification.NotificationResponse;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.repository.NotificationRepository;
import com.projeto.integrador.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationQueryService(NotificationRepository notificationRepository,
                                     UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    private UUID getUserId(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"))
            .getId();
    }

    public List<NotificationResponse> getUserNotifications(String email) {
        UUID userId = getUserId(email);
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public void markAsRead(String email, UUID notificationId) {
        UUID userId = getUserId(email);
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String email) {
        UUID userId = getUserId(email);
        notificationRepository.markAllAsReadByUserId(userId);
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getOrder().getId(),
            n.getType().name(), n.getMessage(), n.isRead(), n.getSentAt());
    }
}
