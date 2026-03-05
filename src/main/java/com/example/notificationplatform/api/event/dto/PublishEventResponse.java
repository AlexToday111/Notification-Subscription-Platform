package com.example.notificationplatform.api.event.dto;

import com.example.notificationplatform.domain.event.EventType;

import java.time.Instant;
import java.util.UUID;

public record PublishEventResponse(
        UUID id,
        UUID incomingEventId,
        String externalEventId,
        String producer,
        EventType type,
        String payload,
        String source,
        Instant createdAt,
        boolean duplicate,
        String status
) {
}
