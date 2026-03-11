package com.example.notificationplatform.infrastructure.persistence.notification;

import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.domain.notification.NotificationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @EntityGraph(attributePaths = {"user", "event", "subscription"})
    List<Notification> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"user", "event", "subscription"})
    List<Notification> findByStatusOrderByUpdatedAtDesc(NotificationStatus status);
}
