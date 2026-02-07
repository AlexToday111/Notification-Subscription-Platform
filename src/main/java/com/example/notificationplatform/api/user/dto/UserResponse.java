package com.example.notificationplatform.api.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String name,
        Instant createdAt
) {}
