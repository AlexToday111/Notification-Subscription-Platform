package com.example.notificationplatform.application.notification;

import com.example.notificationplatform.domain.event.AppEvent;
import com.example.notificationplatform.domain.event.EventType;
import com.example.notificationplatform.infrastructure.persistence.event.AppEventRepository;
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

@Service
public class NotificationGeneratorService {

    private final AppEventRepository eventRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    public NotificationGeneratorService(
            AppEventRepository eventRepository,
            SubscriptionRepository subscriptionRepository,
            NotificationRepository notificationRepository,
            ObjectMapper objectMapper
    ) {
        this.eventRepository = eventRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handle(EventOccurredMessage msg) {
        if (msg == null) throw new IllegalArgumentException("msg is null");
        EventType type = EventType.from(msg.eventType());
        String payload = serializePayload(msg.payload());

        AppEvent saved = eventRepository.save(new AppEvent(type, payload, "rabbit"));
        List<Subscription> subs = subscriptionRepository.findByEventTypeAndActiveTrue(type);
        List<Notification> notifications = new ArrayList<>(subs.size());
        for (Subscription sub : subs) {
            String content = buildContent(saved, sub);
            notifications.add(Notification.newFrom(saved, sub, content));
        }
        notificationRepository.saveAll(notifications);
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
}

