package com.example.notificationplatform.notification.api.dto;

import com.example.notificationplatform.notification.domain.NotificationStatus;
import com.example.notificationplatform.subscription.domain.Channel;

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
