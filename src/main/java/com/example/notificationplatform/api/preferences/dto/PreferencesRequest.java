package com.example.notificationplatform.api.preferences.dto;

import com.example.notificationplatform.domain.preferences.DigestMode;
import com.example.notificationplatform.domain.subscription.Channel;

import java.time.LocalTime;
import java.util.Set;

public record PreferencesRequest(
        Set<Channel> allowedChannels,
        Channel preferredChannel,
        LocalTime quietHoursStart,
        LocalTime quietHoursEnd,
        String timezone,
        DigestMode digestMode
) {
}
