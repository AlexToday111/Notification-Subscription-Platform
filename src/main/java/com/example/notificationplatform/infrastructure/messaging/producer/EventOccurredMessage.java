package com.example.notificationplatform.infrastructure.messaging.producer;

import java.time.Instant;
import java.util.Map;

public record EventOccurredMessage(
        String eventType,
        String entityId,
        Instant occurredAt,
        Map<String, Object> payload
) {
}

