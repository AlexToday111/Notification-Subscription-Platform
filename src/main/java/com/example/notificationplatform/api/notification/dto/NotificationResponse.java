package com.example.notificationplatform.api.notification.dto;

import com.example.notificationplatform.domain.notification.NotificationStatus;
import com.example.notificationplatform.domain.subscription.Channel;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        UUID eventId,
        UUID subscriptionId,
        Channel channel,
        String destination,
        NotificationStatus status,
        String content,
        Instant createdAt
) {}
