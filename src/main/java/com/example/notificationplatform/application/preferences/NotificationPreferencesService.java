package com.example.notificationplatform.application.preferences;

import com.example.notificationplatform.application.audit.AuditService;
import com.example.notificationplatform.application.exception.NotFoundException;
import com.example.notificationplatform.domain.preferences.DigestMode;
import com.example.notificationplatform.domain.preferences.NotificationPreferences;
import com.example.notificationplatform.domain.subscription.Channel;
import com.example.notificationplatform.domain.user.User;
import com.example.notificationplatform.infrastructure.persistence.preferences.NotificationPreferencesRepository;
import com.example.notificationplatform.infrastructure.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPreferencesService {

    private final NotificationPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public NotificationPreferences get(UUID userId) {
        return preferencesRepository.findById(userId)
                .orElseGet(() -> NotificationPreferences.defaults(resolveUser(userId)));
    }

    @Transactional
    public NotificationPreferences update(UUID userId,
                                          Set<Channel> allowedChannels,
                                          Channel preferredChannel,
                                          LocalTime quietHoursStart,
                                          LocalTime quietHoursEnd,
                                          String timezone,
                                          DigestMode digestMode) {
        NotificationPreferences preferences = preferencesRepository.findById(userId)
                .orElseGet(() -> NotificationPreferences.defaults(resolveUser(userId)));
        preferences.update(allowedChannels, preferredChannel, quietHoursStart, quietHoursEnd, timezone, digestMode);
        NotificationPreferences saved = preferencesRepository.save(preferences);
        auditService.record("PREFERENCES_UPDATED", "NotificationPreferences", userId.toString(), "digestMode=" + saved.getDigestMode());
        return saved;
    }

    private User resolveUser(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }
}
