package com.example.notificationplatform.api.subscription.dto;

import com.example.notificationplatform.domain.event.EventType;
import com.example.notificationplatform.domain.subscription.Channel;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        UUID userId,
        EventType eventType,
        Channel channel,
        String destination,
        boolean active,
        Instant createdAt
) {}
