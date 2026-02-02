package com.example.notificationplatform.event.service;

import com.example.notificationplatform.event.api.dto.PingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class PingService {

    private final String version;

    public PingService(@Value("${app.version:dev}") String version) {
        this.version = version;
    }

    public PingResponse ping() {
        return new PingResponse(
                "ok",
                "notification-platform",
                version,
                Instant.now(),
                Map.of()
        );
    }
}
