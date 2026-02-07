package com.example.notificationplatform.api.event.dto;

import com.example.notificationplatform.domain.event.EventType;

import java.time.Instant;
import java.util.UUID;

public record AppEventResponse(
        UUID id,
        EventType type,
        String payload,
        String source,
        Instant createdAt
) {}
