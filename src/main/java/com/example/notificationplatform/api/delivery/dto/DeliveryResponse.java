package com.example.notificationplatform.api.delivery.dto;

import com.example.notificationplatform.domain.notification.NotificationStatus;
import com.example.notificationplatform.domain.subscription.Channel;

import java.time.Instant;
import java.util.UUID;

public record DeliveryResponse(
        UUID id,
        UUID userId,
        UUID eventId,
        Channel channel,
        String destination,
        NotificationStatus status,
        int retryCount,
        Instant nextRetryAt,
        String errorMessage,
        String correlationId,
        Instant createdAt,
        Instant updatedAt
) {
}
