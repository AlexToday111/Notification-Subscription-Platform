package com.example.notificationplatform.application.notification;

import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.infrastructure.persistence.notification.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> listByUser(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }
}
