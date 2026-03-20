package com.example.notificationplatform.application.notification;

import com.example.notificationplatform.application.outbox.OutboxService;
import com.example.notificationplatform.application.rules.RuleMatcher;
import com.example.notificationplatform.domain.event.AppEvent;
import com.example.notificationplatform.domain.event.EventType;
import com.example.notificationplatform.domain.preferences.DigestMode;
import com.example.notificationplatform.domain.preferences.NotificationDigestItem;
import com.example.notificationplatform.domain.preferences.NotificationPreferences;
import com.example.notificationplatform.infrastructure.persistence.event.AppEventRepository;
import com.example.notificationplatform.infrastructure.messaging.producer.EventOccurredMessage;
import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.infrastructure.persistence.notification.NotificationRepository;
import com.example.notificationplatform.infrastructure.messaging.producer.DeliveryRequestMessage;
import com.example.notificationplatform.infrastructure.metrics.NotificationMetrics;
import com.example.notificationplatform.infrastructure.persistence.preferences.NotificationDigestItemRepository;
import com.example.notificationplatform.infrastructure.persistence.preferences.NotificationPreferencesRepository;
import com.example.notificationplatform.domain.subscription.Subscription;
import com.example.notificationplatform.infrastructure.persistence.subscription.SubscriptionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class NotificationGeneratorService {

    private final AppEventRepository eventRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final RuleMatcher ruleMatcher;
    private final NotificationPreferencesRepository preferencesRepository;
    private final NotificationDigestItemRepository digestItemRepository;
    private final NotificationMetrics metrics;

    public NotificationGeneratorService(
            AppEventRepository eventRepository,
            SubscriptionRepository subscriptionRepository,
            NotificationRepository notificationRepository,
            OutboxService outboxService,
            ObjectMapper objectMapper,
            RuleMatcher ruleMatcher,
            NotificationPreferencesRepository preferencesRepository,
            NotificationDigestItemRepository digestItemRepository,
            NotificationMetrics metrics
    ) {
        this.eventRepository = eventRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationRepository = notificationRepository;
        this.outboxService = outboxService;
        this.objectMapper = objectMapper;
        this.ruleMatcher = ruleMatcher;
        this.preferencesRepository = preferencesRepository;
        this.digestItemRepository = digestItemRepository;
        this.metrics = metrics;
    }

    @Transactional
    public void handle(EventOccurredMessage msg) {
        if (msg == null) throw new IllegalArgumentException("msg is null");
        EventType type = EventType.from(msg.eventType());
        AppEvent event = resolveEvent(msg, type);

        List<Subscription> subs = subscriptionRepository.findByEventTypeAndActiveTrue(type);
        if (subs.isEmpty()) {
            return;
        }

        List<Notification> notifications = new ArrayList<>(subs.size());
        for (Subscription sub : subs) {
            boolean matched;
            try {
                matched = metrics.recordRuleEvaluation(() -> ruleMatcher.matches(sub, event.getPayload()));
            } catch (Exception e) {
                matched = false;
            }
            if (!matched) {
                continue;
            }
            metrics.incSubscriptionMatched();
            String content = buildContent(event, sub);
            notifications.add(Notification.newFrom(event, sub, content, msg.correlationId()));
        }
        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        for (Notification notification : savedNotifications) {
            metrics.incNotificationCreated();
            NotificationPreferences preferences = preferencesRepository.findById(notification.getUser().getId())
                    .orElseGet(() -> NotificationPreferences.defaults(notification.getUser()));
            if (!preferences.allows(notification.getChannel())) {
                notification.markFailed("Channel disabled by user preferences");
                log.info("Notification skipped because channel is disabled notificationId={} channel={}",
                        notification.getId(), notification.getChannel());
                continue;
            }
            if (preferences.isQuietHours(Instant.now()) || preferences.getDigestMode() != DigestMode.IMMEDIATE) {
                notification.markRetrying("Queued for digest or quiet hours");
                digestItemRepository.save(NotificationDigestItem.queue(
                        notification,
                        preferences.getDigestMode(),
                        digestAvailableAt(preferences.getDigestMode())
                ));
                log.info("Notification queued for digest notificationId={} digestMode={}",
                        notification.getId(), preferences.getDigestMode());
                continue;
            }
            notification.markQueued();
            outboxService.enqueue(
                    "Notification",
                    notification.getId().toString(),
                    OutboxService.DELIVERY_REQUESTED,
                    new DeliveryRequestMessage(notification.getId(), notification.getCorrelationId())
            );
            log.info("Notification created and queued notificationId={} eventId={} channel={}",
                    notification.getId(), event.getId(), notification.getChannel());
        }
    }

    private Instant digestAvailableAt(DigestMode digestMode) {
        Instant now = Instant.now();
        return switch (digestMode) {
            case HOURLY -> now.plus(Duration.ofHours(1));
            case DAILY -> now.plus(Duration.ofDays(1));
            case IMMEDIATE -> now.plus(Duration.ofHours(1));
        };
    }

    private String buildContent(AppEvent event, Subscription sub) {
        return "Event " + event.getType() + ": " + event.getPayload();
    }

    private String serializePayload(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("payload is not serializable", e);
        }
    }

    private AppEvent resolveEvent(EventOccurredMessage msg, EventType type) {
        UUID eventId = parseEventId(msg.entityId());
        if (eventId != null) {
            return eventRepository.findById(eventId)
                    .orElseGet(() -> eventRepository.save(
                            new AppEvent(type, serializePayload(msg.payload()), "rabbit")
                    ));
        }
        return eventRepository.save(new AppEvent(type, serializePayload(msg.payload()), "rabbit"));
    }

    private UUID parseEventId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
