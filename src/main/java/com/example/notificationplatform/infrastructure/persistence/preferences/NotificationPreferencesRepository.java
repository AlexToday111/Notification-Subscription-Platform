package com.example.notificationplatform.infrastructure.persistence.preferences;

import com.example.notificationplatform.domain.preferences.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, UUID> {
}
