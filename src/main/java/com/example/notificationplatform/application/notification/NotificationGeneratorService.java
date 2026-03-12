package com.example.notificationplatform.application.notification;

import com.example.notificationplatform.domain.event.AppEvent;
import com.example.notificationplatform.domain.event.EventType;
import com.example.notificationplatform.infrastructure.persistence.event.AppEventRepository;
import com.example.notificationplatform.infrastructure.messaging.producer.DeliveryPublisher;
import com.example.notificationplatform.infrastructure.messaging.producer.EventOccurredMessage;
import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.infrastructure.persistence.notification.NotificationRepository;
import com.example.notificationplatform.domain.subscription.Subscription;
import com.example.notificationplatform.infrastructure.persistence.subscription.SubscriptionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationGeneratorService {

    private final AppEventRepository eventRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;
    private final DeliveryPublisher deliveryPublisher;
    private final ObjectMapper objectMapper;

    public NotificationGeneratorService(
            AppEventRepository eventRepository,
            SubscriptionRepository subscriptionRepository,
            NotificationRepository notificationRepository,
            DeliveryPublisher deliveryPublisher,
            ObjectMapper objectMapper
    ) {
        this.eventRepository = eventRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationRepository = notificationRepository;
        this.deliveryPublisher = deliveryPublisher;
        this.objectMapper = objectMapper;
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
            String content = buildContent(event, sub);
            notifications.add(Notification.newFrom(event, sub, content));
        }
        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        savedNotifications.forEach(notification -> deliveryPublisher.publish(notification.getId()));
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
