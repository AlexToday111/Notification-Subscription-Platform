package com.example.notificationplatform.domain.outbox;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "outbox_messages", indexes = {
        @Index(name = "ix_outbox_messages_status_created_at", columnList = "status, created_at")
})
public class OutboxMessage {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 80)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 120)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "text")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OutboxStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @Column(name = "correlation_id", length = 120)
    private String correlationId;

    public static OutboxMessage pending(String aggregateType, String aggregateId, String eventType, String payload, String correlationId) {
        if (aggregateType == null || aggregateType.isBlank()) throw new IllegalArgumentException("aggregateType is blank");
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId is blank");
        if (eventType == null || eventType.isBlank()) throw new IllegalArgumentException("eventType is blank");
        if (payload == null || payload.isBlank()) throw new IllegalArgumentException("payload is blank");

        OutboxMessage message = new OutboxMessage();
        message.id = UUID.randomUUID();
        message.aggregateType = aggregateType.trim();
        message.aggregateId = aggregateId.trim();
        message.eventType = eventType.trim();
        message.payload = payload.trim();
        message.status = OutboxStatus.PENDING;
        message.createdAt = Instant.now();
        message.retryCount = 0;
        message.correlationId = correlationId == null || correlationId.isBlank() ? null : correlationId.trim();
        return message;
    }

    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = Instant.now();
        this.lastError = null;
    }

    public void markFailed(String error) {
        this.status = OutboxStatus.FAILED;
        this.retryCount++;
        this.lastError = sanitize(error);
    }

    public void retry() {
        if (this.status == OutboxStatus.FAILED) {
            this.status = OutboxStatus.PENDING;
        }
    }

    private String sanitize(String error) {
        if (error == null || error.isBlank()) {
            return null;
        }
        String trimmed = error.trim();
        return trimmed.length() <= 1000 ? trimmed : trimmed.substring(0, 1000);
    }
}
