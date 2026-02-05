package com.example.notificationplatform.messaging.producer;

import java.time.Instant;
import java.util.UUID;

public record NotificationFailedMessage (
        UUID notificationId,
        String reason,
        int retryCount,
        Instant failedAt
){
    public static NotificationFailedMessage of(UUID id, String reason, int retryCount){
        return new NotificationFailedMessage(id, reason, retryCount, Instant.now());
    }
}
