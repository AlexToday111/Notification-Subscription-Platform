package com.example.notificationplatform.api.preferences.dto;

import com.example.notificationplatform.domain.preferences.DigestMode;
import com.example.notificationplatform.domain.subscription.Channel;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record PreferencesResponse(
        UUID userId,
        Set<Channel> allowedChannels,
        Channel preferredChannel,
        LocalTime quietHoursStart,
        LocalTime quietHoursEnd,
        String timezone,
        DigestMode digestMode,
        Instant updatedAt
) {
}
