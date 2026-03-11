package com.example.notificationplatform.domain.notification;

import com.example.notificationplatform.domain.subscription.Channel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "delivery_attempts", indexes = {
        @Index(name = "ix_delivery_attempts_notification_started_at", columnList = "notification_id, started_at")
})
public class DeliveryAttempt {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_delivery_attempts_notification"))
    private Notification notification;

    @Column(name = "attempt_number", nullable = false)
    private int attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 32)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private DeliveryAttemptStatus status;

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "error_code", length = 120)
    private String errorCode;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "response_diagnostic", length = 1000)
    private String responseDiagnostic;

    @Column(name = "retryable", nullable = false)
    private boolean retryable;

    @Column(name = "correlation_id", length = 120)
    private String correlationId;

    public static DeliveryAttempt started(Notification notification, int attemptNumber) {
        if (notification == null) throw new IllegalArgumentException("notification is null");
        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.id = UUID.randomUUID();
        attempt.notification = notification;
        attempt.attemptNumber = attemptNumber;
        attempt.channel = notification.getChannel();
        attempt.status = DeliveryAttemptStatus.STARTED;
        attempt.startedAt = Instant.now();
        attempt.retryable = false;
        attempt.correlationId = notification.getCorrelationId();
        return attempt;
    }

    public void markSucceeded(Integer responseStatus, String diagnostic) {
        this.status = DeliveryAttemptStatus.SUCCEEDED;
        this.completedAt = Instant.now();
        this.responseStatus = responseStatus;
        this.responseDiagnostic = sanitize(diagnostic);
        this.retryable = false;
    }

    public void markFailed(String code, String message, Integer responseStatus, String diagnostic, boolean retryable) {
        this.status = DeliveryAttemptStatus.FAILED;
        this.completedAt = Instant.now();
        this.errorCode = sanitizeShort(code, 120);
        this.errorMessage = sanitize(message);
        this.responseStatus = responseStatus;
        this.responseDiagnostic = sanitize(diagnostic);
        this.retryable = retryable;
    }

    private String sanitize(String value) {
        return sanitizeShort(value, 1000);
    }

    private String sanitizeShort(String value, int limit) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() <= limit ? trimmed : trimmed.substring(0, limit);
    }
}
