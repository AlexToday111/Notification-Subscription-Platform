package com.example.notificationplatform.event.api.dto;

import com.example.notificationplatform.event.domain.EventType;

import java.time.Instant;
import java.util.UUID;

public record AppEventResponse(
        UUID id,
        EventType type,
        String payload,
        String source,
        Instant createdAt
) {}
