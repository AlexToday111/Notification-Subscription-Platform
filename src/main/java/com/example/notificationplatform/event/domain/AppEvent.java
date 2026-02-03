package com.example.notificationplatform.event.domain;

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
        name = "events",
        indexes = {
                @Index(name = "ix_events_type_created_at", columnList = "type, created_at")
        }
)
public class AppEvent {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 64)
    private EventType type;

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "source", nullable = false, length = 64)
    private String source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public AppEvent(EventType type, String payload, String source) {
        if (type == null) throw new IllegalArgumentException("type is null");
        if (payload == null || payload.isBlank()) throw new IllegalArgumentException("payload is blank");
        if (source == null || source.isBlank()) throw new IllegalArgumentException("source is blank");

        this.id = UUID.randomUUID();
        this.type = type;
        this.payload = payload.trim();
        this.source = source.trim();
        this.createdAt = Instant.now();
    }
}
