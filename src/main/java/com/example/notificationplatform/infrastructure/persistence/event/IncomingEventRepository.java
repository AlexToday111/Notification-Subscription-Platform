package com.example.notificationplatform.infrastructure.persistence.event;

import com.example.notificationplatform.domain.event.IncomingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IncomingEventRepository extends JpaRepository<IncomingEvent, UUID> {
    Optional<IncomingEvent> findByProducerAndExternalEventId(String producer, String externalEventId);
}
