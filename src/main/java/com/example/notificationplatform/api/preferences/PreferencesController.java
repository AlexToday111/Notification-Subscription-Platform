package com.example.notificationplatform.api.preferences;

import com.example.notificationplatform.api.preferences.dto.PreferencesRequest;
import com.example.notificationplatform.api.preferences.dto.PreferencesResponse;
import com.example.notificationplatform.application.preferences.NotificationPreferencesService;
import com.example.notificationplatform.domain.preferences.NotificationPreferences;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/{userId}/preferences")
public class PreferencesController {

    private final NotificationPreferencesService service;

    @GetMapping
    public PreferencesResponse get(@PathVariable UUID userId) {
        return toResponse(service.get(userId));
    }

    @PutMapping
    public PreferencesResponse update(@PathVariable UUID userId, @RequestBody PreferencesRequest request) {
        return toResponse(service.update(
                userId,
                request.allowedChannels(),
                request.preferredChannel(),
                request.quietHoursStart(),
                request.quietHoursEnd(),
                request.timezone(),
                request.digestMode()
        ));
    }

    private PreferencesResponse toResponse(NotificationPreferences preferences) {
        return new PreferencesResponse(
                preferences.getUserId(),
                preferences.allowedChannelSet(),
                preferences.getPreferredChannel(),
                preferences.getQuietHoursStart(),
                preferences.getQuietHoursEnd(),
                preferences.getTimezone(),
                preferences.getDigestMode(),
                preferences.getUpdatedAt()
        );
    }
}
