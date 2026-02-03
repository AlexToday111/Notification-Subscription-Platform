package com.example.notificationplatform.subscription.api.dto;

import com.example.notificationplatform.event.domain.EventType;
import com.example.notificationplatform.subscription.domain.Channel;

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
