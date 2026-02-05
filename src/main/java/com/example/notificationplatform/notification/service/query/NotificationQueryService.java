package com.example.notificationplatform.notification.service.query;

import com.example.notificationplatform.notification.domain.Notification;
import com.example.notificationplatform.notification.repo.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> listByUser(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }
}
