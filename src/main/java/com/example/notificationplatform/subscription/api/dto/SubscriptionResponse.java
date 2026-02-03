package com.example.notificationplatform.subscription.api.dto;

import com.example.notificationplatform.event.domain.EventType;
import com.example.notificationplatform.subscription.domain.Channel;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionResponse {
    private final UUID id;
    private final UUID userId;
    private final EventType eventType;
    private final Channel channel;
    private final String destination;
    private final boolean active;
    private final Instant createdAt;

    public SubscriptionResponse(UUID id, UUID userId, EventType eventType, Channel channel, String destination, boolean active, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.channel = channel;
        this.destination = destination;
        this.active = active;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getDestination() {
        return destination;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
