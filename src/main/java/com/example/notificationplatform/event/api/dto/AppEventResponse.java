package com.example.notificationplatform.event.api.dto;

import com.example.notificationplatform.event.domain.EventType;

import java.time.Instant;
import java.util.UUID;

public class AppEventResponse {
    private final UUID id;
    private final EventType type;
    private final String payload;
    private final String source;
    private final Instant createdAt;

    public AppEventResponse(UUID id, EventType type, String payload, String source, Instant createdAt) {
        this.id = id;
        this.type = type;
        this.payload = payload;
        this.source = source;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public EventType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public String getSource() {
        return source;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
