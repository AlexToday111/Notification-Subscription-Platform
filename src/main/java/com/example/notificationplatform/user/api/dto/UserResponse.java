package com.example.notificationplatform.user.api.dto;

import java.time.Instant;
import java.util.UUID;

public class UserResponse {
    private final UUID id;
    private final String email;
    private final String name;
    private final Instant createdAt;

    public UserResponse(UUID id, String email, String name, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
