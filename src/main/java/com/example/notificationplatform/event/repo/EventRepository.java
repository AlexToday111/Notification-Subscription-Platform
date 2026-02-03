package com.example.notificationplatform.event.repo;

import com.example.notificationplatform.event.domain.AppEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<AppEvent, UUID> {
}
