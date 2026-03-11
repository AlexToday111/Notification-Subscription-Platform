package com.example.notificationplatform.infrastructure.persistence.notification;

import com.example.notificationplatform.domain.notification.DeliveryAttempt;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttempt, UUID> {

    @EntityGraph(attributePaths = {"notification"})
    List<DeliveryAttempt> findByNotification_IdOrderByStartedAtAsc(UUID notificationId);
}
