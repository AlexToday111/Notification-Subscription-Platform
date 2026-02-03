package com.example.notificationplatform.event.service;

import com.example.notificationplatform.event.domain.AppEvent;
import com.example.notificationplatform.event.repo.AppEventRepository;
import com.example.notificationplatform.event.service.command.PublishEventCommand;
import com.example.notificationplatform.notification.domain.Notification;
import com.example.notificationplatform.notification.repo.NotificationRepository;
import com.example.notificationplatform.subscription.domain.Subscription;
import com.example.notificationplatform.subscription.repo.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    private final AppEventRepository eventRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;

    public EventService(AppEventRepository eventRepository, SubscriptionRepository subscriptionRepository, NotificationRepository notificationRepository) {
        this.eventRepository = eventRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public AppEvent publish(PublishEventCommand cmd){
        if (cmd == null) throw new IllegalArgumentException("command is null");
        if (cmd.type() == null) throw new IllegalArgumentException("type is null");
        if (cmd.payload() == null || cmd.payload().isBlank()) throw new IllegalArgumentException("payload is blank");

        String source = (cmd.source() == null || cmd.source().isBlank()) ? "api" : cmd.source().trim();
        String payload = cmd.payload().trim();

        AppEvent saved = eventRepository.save(new AppEvent(cmd.type(), payload, source));
        List<Subscription> subs = subscriptionRepository.findByEventTypeAndActiveTrue(cmd.type());
        List<Notification> notifications = new ArrayList<>(subs.size());
        for (Subscription sub : subs){
            String content = buildContent(saved, sub);
            notifications.add(Notification.newFrom(saved, sub, content));
        }

        notificationRepository.saveAll(notifications);

        return saved;

    }
    private String buildContent(AppEvent event, Subscription sub){
        return "Event " + event.getType() + ": " + event.getPayload();
    }
}