package com.example.notificationplatform.infrastructure.persistence.preferences;

import com.example.notificationplatform.domain.preferences.NotificationDigestItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationDigestItemRepository extends JpaRepository<NotificationDigestItem, UUID> {
}
