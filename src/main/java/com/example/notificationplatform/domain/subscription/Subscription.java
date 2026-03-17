package com.example.notificationplatform.domain.subscription;

import com.example.notificationplatform.domain.event.EventType;
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
@Table(
        name = "subscriptions",
        indexes = {
                @Index(name = "ix_subscriptions_user_id", columnList = "user_id"),
                @Index(name = "ix_subscriptions_event_type_active", columnList = "event_type, active")
        }
)

public class Subscription {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_subscriptions_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 64)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 32)
    private Channel channel;

    @Column(name = "destination", nullable = false, length = 512)
    private String destination;

    @Column(name = "condition_json", columnDefinition = "text")
    private String conditionJson;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Subscription(User user, EventType eventType, Channel channel, String destination, String conditionJson){
        if (user == null) throw new IllegalArgumentException("user is null");
        if (eventType == null) throw new IllegalArgumentException("eventType is null");
        if (channel == null) throw new IllegalArgumentException("channel is null");
        if (destination == null || destination.isBlank()) throw new IllegalArgumentException("destination is blank");

        String dest = destination.trim();
        if (!channel.isValidDestination(dest)) {
            throw new IllegalArgumentException("Invalid destination for channel " + channel + ": " + destination);
        }
        this.id = UUID.randomUUID();
        this.user = user;
        this.eventType = eventType;
        this.channel = channel;
        this.destination = dest;
        this.conditionJson = normalizeCondition(conditionJson);
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public Subscription(User user, EventType eventType, Channel channel, String destination){
        this(user, eventType, channel, destination, null);
    }
    public void deactivate(){
        this.active = false;
        this.updatedAt = Instant.now();
    }

    @Deprecated(forRemoval = false)
    public void deactive(){
        deactivate();
    }
    public void activate(){
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void updateCondition(String conditionJson) {
        this.conditionJson = normalizeCondition(conditionJson);
        this.updatedAt = Instant.now();
    }

    private String normalizeCondition(String conditionJson) {
        if (conditionJson == null || conditionJson.isBlank()) {
            return null;
        }
        return conditionJson.trim();
    }
}
