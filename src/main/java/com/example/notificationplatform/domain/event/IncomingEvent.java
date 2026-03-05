package com.example.notificationplatform.domain.event;

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
        name = "incoming_events",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_incoming_events_producer_external",
                columnNames = {"producer", "external_event_id"}
        ),
        indexes = @Index(name = "ix_incoming_events_status_received_at", columnList = "status, received_at")
)
public class IncomingEvent {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "external_event_id", nullable = false, length = 160)
    private String externalEventId;

    @Column(name = "producer", nullable = false, length = 120)
    private String producer;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 64)
    private EventType type;

    @Column(name = "payload", nullable = false, columnDefinition = "text")
    private String payload;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private IncomingEventStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_event_id", foreignKey = @ForeignKey(name = "fk_incoming_events_app_event"))
    private AppEvent appEvent;

    @Column(name = "correlation_id", length = 120)
    private String correlationId;

    public IncomingEvent(String externalEventId, String producer, EventType type, String payload, String correlationId) {
        if (externalEventId == null || externalEventId.isBlank()) throw new IllegalArgumentException("externalEventId is blank");
        if (producer == null || producer.isBlank()) throw new IllegalArgumentException("producer is blank");
        if (type == null) throw new IllegalArgumentException("type is null");
        if (payload == null || payload.isBlank()) throw new IllegalArgumentException("payload is blank");

        this.id = UUID.randomUUID();
        this.externalEventId = externalEventId.trim();
        this.producer = producer.trim();
        this.type = type;
        this.payload = payload.trim();
        this.receivedAt = Instant.now();
        this.status = IncomingEventStatus.RECEIVED;
        this.correlationId = normalize(correlationId);
    }

    public void markProcessed(AppEvent appEvent) {
        this.appEvent = appEvent;
        this.status = IncomingEventStatus.PROCESSED;
        this.processedAt = Instant.now();
    }

    public void markFailed(String ignoredReason) {
        this.status = IncomingEventStatus.FAILED;
        this.processedAt = Instant.now();
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
