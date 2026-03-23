package com.example.notificationplatform.domain.audit;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "ix_audit_logs_created_at", columnList = "created_at"),
        @Index(name = "ix_audit_logs_action_created_at", columnList = "action, created_at")
})
public class AuditLog {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "actor_user_id", length = 160)
    private String actorUserId;

    @Column(name = "action", nullable = false, length = 120)
    private String action;

    @Column(name = "entity_type", nullable = false, length = 80)
    private String entityType;

    @Column(name = "entity_id", length = 120)
    private String entityId;

    @Column(name = "details", columnDefinition = "text")
    private String details;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "correlation_id", length = 120)
    private String correlationId;

    public static AuditLog record(String actorUserId, String action, String entityType, String entityId, String details, String correlationId) {
        AuditLog log = new AuditLog();
        log.id = UUID.randomUUID();
        log.actorUserId = actorUserId == null || actorUserId.isBlank() ? "system" : actorUserId.trim();
        log.action = action;
        log.entityType = entityType;
        log.entityId = entityId;
        log.details = details;
        log.createdAt = Instant.now();
        log.correlationId = correlationId;
        return log;
    }
}
