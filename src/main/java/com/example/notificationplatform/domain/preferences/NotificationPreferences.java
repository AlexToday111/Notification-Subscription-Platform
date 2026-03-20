package com.example.notificationplatform.domain.preferences;

import com.example.notificationplatform.domain.subscription.Channel;
import com.example.notificationplatform.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_notification_preferences_user"))
    private User user;

    @Column(name = "allowed_channels", nullable = false, length = 256)
    private String allowedChannels;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_channel", length = 32)
    private Channel preferredChannel;

    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;

    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;

    @Column(name = "timezone", nullable = false, length = 80)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(name = "digest_mode", nullable = false, length = 32)
    private DigestMode digestMode;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static NotificationPreferences defaults(User user) {
        NotificationPreferences preferences = new NotificationPreferences();
        preferences.user = user;
        preferences.userId = user.getId();
        preferences.allowedChannels = encodeAllowed(EnumSet.allOf(Channel.class));
        preferences.preferredChannel = Channel.EMAIL;
        preferences.timezone = "UTC";
        preferences.digestMode = DigestMode.IMMEDIATE;
        preferences.updatedAt = Instant.now();
        return preferences;
    }

    public void update(Set<Channel> allowedChannels,
                       Channel preferredChannel,
                       LocalTime quietHoursStart,
                       LocalTime quietHoursEnd,
                       String timezone,
                       DigestMode digestMode) {
        Set<Channel> allowed = allowedChannels == null || allowedChannels.isEmpty()
                ? EnumSet.allOf(Channel.class)
                : EnumSet.copyOf(allowedChannels);
        if (preferredChannel != null && !allowed.contains(preferredChannel)) {
            throw new IllegalArgumentException("preferredChannel must be allowed");
        }
        ZoneId.of(timezone == null || timezone.isBlank() ? "UTC" : timezone.trim());
        this.allowedChannels = encodeAllowed(allowed);
        this.preferredChannel = preferredChannel;
        this.quietHoursStart = quietHoursStart;
        this.quietHoursEnd = quietHoursEnd;
        this.timezone = timezone == null || timezone.isBlank() ? "UTC" : timezone.trim();
        this.digestMode = digestMode == null ? DigestMode.IMMEDIATE : digestMode;
        this.updatedAt = Instant.now();
    }

    public Set<Channel> allowedChannelSet() {
        if (allowedChannels == null || allowedChannels.isBlank()) {
            return EnumSet.noneOf(Channel.class);
        }
        return Arrays.stream(allowedChannels.split(","))
                .filter(s -> !s.isBlank())
                .map(Channel::from)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Channel.class)));
    }

    public boolean allows(Channel channel) {
        return allowedChannelSet().contains(channel);
    }

    public boolean isQuietHours(Instant instant) {
        if (quietHoursStart == null || quietHoursEnd == null) {
            return false;
        }
        LocalTime local = instant.atZone(ZoneId.of(timezone)).toLocalTime();
        if (quietHoursStart.equals(quietHoursEnd)) {
            return true;
        }
        if (quietHoursStart.isBefore(quietHoursEnd)) {
            return !local.isBefore(quietHoursStart) && local.isBefore(quietHoursEnd);
        }
        return !local.isBefore(quietHoursStart) || local.isBefore(quietHoursEnd);
    }

    private static String encodeAllowed(Set<Channel> channels) {
        return channels.stream().map(Enum::name).sorted().collect(Collectors.joining(","));
    }
}
