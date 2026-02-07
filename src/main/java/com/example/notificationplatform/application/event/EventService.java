package com.example.notificationplatform.application.event;

import com.example.notificationplatform.domain.event.AppEvent;
import com.example.notificationplatform.infrastructure.persistence.event.AppEventRepository;
import com.example.notificationplatform.application.event.command.PublishEventCommand;
import com.example.notificationplatform.infrastructure.messaging.producer.EventOccurredMessage;
import com.example.notificationplatform.infrastructure.messaging.producer.EventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class EventService {
    private final AppEventRepository eventRepository;
    private final EventPublisher eventPublisher;

    public EventService(AppEventRepository eventRepository, EventPublisher eventPublisher) {
        this.eventRepository = eventRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AppEvent publish(PublishEventCommand cmd){
        if (cmd == null) throw new IllegalArgumentException("command is null");
        if (cmd.type() == null) throw new IllegalArgumentException("type is null");
        if (cmd.payload() == null || cmd.payload().isBlank()) throw new IllegalArgumentException("payload is blank");

        String source = (cmd.source() == null || cmd.source().isBlank()) ? "api" : cmd.source().trim();
        String payload = cmd.payload().trim();

        AppEvent saved = eventRepository.save(new AppEvent(cmd.type(), payload, source));
        EventOccurredMessage msg = new EventOccurredMessage(
                saved.getType().name(),
                saved.getId().toString(),
                saved.getCreatedAt(),
                Map.of("payload", saved.getPayload(), "source", saved.getSource())
        );
        eventPublisher.publish(msg);

        return saved;

    }
}