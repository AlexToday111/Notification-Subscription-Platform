package com.example.notificationplatform.subscription.api.dto;

import com.example.notificationplatform.event.domain.EventType;
import com.example.notificationplatform.subscription.domain.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateSubscriptionRequest(
        @NotNull UUID userId,
        @NotNull EventType eventType,
        @NotNull Channel channel,
        @NotBlank @Size(max = 512) String destination
) {
}