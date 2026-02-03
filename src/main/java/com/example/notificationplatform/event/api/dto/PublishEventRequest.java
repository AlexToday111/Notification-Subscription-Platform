package com.example.notificationplatform.event.api.dto;

import com.example.notificationplatform.event.domain.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PublishEventRequest(
        @NotNull EventType type,
        @NotBlank String payload,
        @Size(max = 64) String source
) {}
