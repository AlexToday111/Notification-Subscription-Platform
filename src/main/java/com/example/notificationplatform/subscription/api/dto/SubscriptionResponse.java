package com.example.notificationplatform.subscription.api.dto;

import com.example.notificationplatform.event.domain.EventType;
import com.example.notificationplatform.subscription.domain.Channel;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionResponse {
    public SubscriptionResponse(UUID id, UUID id1, EventType eventType, Channel channel, String destination, boolean active, Instant createdAt) {
    }
}