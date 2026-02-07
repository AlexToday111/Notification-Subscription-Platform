package com.example.notificationplatform.application.event;

import com.example.notificationplatform.api.event.dto.PingResponse;
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
