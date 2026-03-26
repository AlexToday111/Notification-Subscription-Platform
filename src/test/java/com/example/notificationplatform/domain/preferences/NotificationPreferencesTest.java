package com.example.notificationplatform.domain.preferences;

import com.example.notificationplatform.domain.subscription.Channel;
import com.example.notificationplatform.domain.user.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NotificationPreferencesTest {

    @Test
    void isQuietHours_handlesWindowCrossingMidnight() {
        NotificationPreferences preferences = NotificationPreferences.defaults(new User("quiet@example.com", "Quiet User"));
        preferences.update(Set.of(Channel.EMAIL), Channel.EMAIL, LocalTime.of(22, 0), LocalTime.of(7, 0), "UTC", DigestMode.HOURLY);

        assertTrue(preferences.isQuietHours(Instant.parse("2026-03-20T23:15:00Z")));
        assertTrue(preferences.isQuietHours(Instant.parse("2026-03-20T05:30:00Z")));
        assertFalse(preferences.isQuietHours(Instant.parse("2026-03-20T12:00:00Z")));
    }

    @Test
    void update_rejectsPreferredChannelOutsideAllowedSet() {
        NotificationPreferences preferences = NotificationPreferences.defaults(new User("pref@example.com", "Pref User"));

        assertThrows(IllegalArgumentException.class, () ->
                preferences.update(Set.of(Channel.EMAIL), Channel.TELEGRAM, null, null, "UTC", DigestMode.IMMEDIATE)
        );
    }
}
