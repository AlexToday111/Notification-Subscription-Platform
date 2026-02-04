package com.example.notificationplatform.messaging;

import java.time.Instant;
import java.util.Map;

public record EventOccurredMessage(
        String eventType,
        String entityId,
        Instant occurredAt,
        Map<String, Object> payload
) {
}
