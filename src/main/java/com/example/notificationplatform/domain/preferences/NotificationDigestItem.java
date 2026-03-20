package com.example.notificationplatform.domain.preferences;

import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_digest_items", indexes = {
        @Index(name = "ix_notification_digest_items_status_available", columnList = "status, available_at")
})
public class NotificationDigestItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_digest_items_user"))
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false, foreignKey = @ForeignKey(name = "fk_digest_items_notification"))
    private Notification notification;

    @Enumerated(EnumType.STRING)
    @Column(name = "digest_mode", nullable = false, length = 32)
    private DigestMode digestMode;

    @Column(name = "available_at", nullable = false)
    private Instant availableAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private DigestItemStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static NotificationDigestItem queue(Notification notification, DigestMode mode, Instant availableAt) {
        NotificationDigestItem item = new NotificationDigestItem();
        item.id = UUID.randomUUID();
        item.user = notification.getUser();
        item.notification = notification;
        item.digestMode = mode;
        item.availableAt = availableAt;
        item.status = DigestItemStatus.QUEUED;
        item.createdAt = Instant.now();
        return item;
    }
}
