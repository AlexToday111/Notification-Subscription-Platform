package com.example.notificationplatform.api.event.dto;

import com.example.notificationplatform.domain.event.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PublishEventRequest(
        @NotNull EventType type,
        @NotBlank String payload,
        @Size(max = 64) String source
) {}
