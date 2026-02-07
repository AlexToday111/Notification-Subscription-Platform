package com.example.notificationplatform.infrastructure.persistence.event;

import com.example.notificationplatform.domain.event.AppEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppEventRepository extends JpaRepository<AppEvent, UUID> {
}
