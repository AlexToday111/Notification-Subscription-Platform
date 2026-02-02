package com.example.notificationplatform.event.api.dto;

import java.time.Instant;
import java.util.Map;

public record PingResponse(
        String status,
        String service,
        String version,
        Instant timestamp,
        Map<String, String> dependencies
) {}
