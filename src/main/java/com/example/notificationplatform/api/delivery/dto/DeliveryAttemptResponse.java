package com.example.notificationplatform.api.delivery.dto;

import com.example.notificationplatform.domain.notification.DeliveryAttemptStatus;
import com.example.notificationplatform.domain.subscription.Channel;

import java.time.Instant;
import java.util.UUID;

public record DeliveryAttemptResponse(
        UUID id,
        UUID notificationId,
        int attemptNumber,
        Channel channel,
        DeliveryAttemptStatus status,
        Instant startedAt,
        Instant completedAt,
        String errorCode,
        String errorMessage,
        Integer responseStatus,
        String responseDiagnostic,
        boolean retryable,
        String correlationId
) {
}
