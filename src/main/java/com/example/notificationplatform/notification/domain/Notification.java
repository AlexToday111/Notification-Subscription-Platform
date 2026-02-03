package com.example.notificationplatform.notification.domain;

import com.example.notificationplatform.event.domain.AppEvent;
import com.example.notificationplatform.subscription.domain.Channel;
import com.example.notificationplatform.subscription.domain.Subscription;
import com.example.notificationplatform.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "ix_notifications_user_created_at", columnList = "user_id, created_at"),
                @Index(name = "ix_notifications_status", columnList = "status")
        }
)
public class Notification {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_notifications_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_notifications_event"))
    private AppEvent event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id",
            foreignKey = @ForeignKey(name = "fk_notifications_subscription"))
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 32)
    private Channel channel;

    @Column(name = "destination", nullable = false, length = 512)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private NotificationStatus status;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Notification newFrom(AppEvent event, Subscription subscription, String content) {
        if (event == null) throw new IllegalArgumentException("event is null");
        if (subscription == null) throw new IllegalArgumentException("subscription is null");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content is blank");

        Notification n = new Notification();
        n.id = UUID.randomUUID();
        n.user = subscription.getUser();
        n.event = event;
        n.subscription = subscription;
        n.channel = subscription.getChannel();
        n.destination = subscription.getDestination();
        n.status = NotificationStatus.NEW;
        n.content = content.trim();
        n.createdAt = Instant.now();
        n.updatedAt = n.createdAt;
        return n;
    }

    public void markSending() {
        this.status = NotificationStatus.SENDING;
        touch();
    }

    public void markSent() {
        this.status = NotificationStatus.SENT;
        this.errorMessage = null;
        touch();
    }

    public void markFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = (errorMessage == null ? null : errorMessage.trim());
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }
}
