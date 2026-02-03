package com.example.notificationplatform.subscription.domain;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * - EMAIL   -> email
 * - TELEGRAM-> chatId / username
 * - WEBHOOK -> URL
 */
public enum Channel {

    EMAIL("Email"),
    TELEGRAM("Telegram"),
    WEBHOOK("Webhook");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private static final Pattern TELEGRAM_PATTERN =
            Pattern.compile("^(?:@?[A-Za-z0-9_]{5,32}|-?\\d{5,20})$");

    private static final Pattern WEBHOOK_PATTERN =
            Pattern.compile("^https?://.+", Pattern.CASE_INSENSITIVE);

    private final String displayName;

    Channel(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public boolean isValidDestination(String destination) {
        if (destination == null || destination.isBlank()) return false;

        return switch (this) {
            case EMAIL -> EMAIL_PATTERN.matcher(destination.trim()).matches();
            case TELEGRAM -> TELEGRAM_PATTERN.matcher(destination.trim()).matches();
            case WEBHOOK -> WEBHOOK_PATTERN.matcher(destination.trim()).matches();
        };
    }

    public static Channel from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Channel is blank");
        }
        String normalized = raw.trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);

        for (Channel c : values()) {
            if (c.name().equals(normalized)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown Channel: " + raw);
    }
}
