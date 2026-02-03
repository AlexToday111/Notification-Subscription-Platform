package com.example.notificationplatform.subscription.domain;

import com.example.notificationplatform.event.domain.EventType;
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

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Subscription(User user, EventType eventType, Channel channel, String destination){
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
        this.active = true;
        this.createdAt = Instant.now();
    }
    public void deactive(){
        this.active = false;
    }
    public void activate(){
        this.active = true;
    }
}