package com.example.notificationplatform.application.event;

import com.example.notificationplatform.application.outbox.OutboxService;
import com.example.notificationplatform.domain.event.AppEvent;
import com.example.notificationplatform.domain.event.IncomingEvent;
import com.example.notificationplatform.infrastructure.persistence.event.AppEventRepository;
import com.example.notificationplatform.application.event.command.PublishEventCommand;
import com.example.notificationplatform.infrastructure.metrics.NotificationMetrics;
import com.example.notificationplatform.infrastructure.messaging.producer.EventOccurredMessage;
import com.example.notificationplatform.infrastructure.persistence.event.IncomingEventRepository;
import com.example.notificationplatform.infrastructure.web.CorrelationIdFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class EventService {
    private final AppEventRepository eventRepository;
    private final IncomingEventRepository incomingEventRepository;
    private final OutboxService outboxService;
    private final NotificationMetrics metrics;

    public EventService(AppEventRepository eventRepository,
                        IncomingEventRepository incomingEventRepository,
                        OutboxService outboxService,
                        NotificationMetrics metrics) {
        this.eventRepository = eventRepository;
        this.incomingEventRepository = incomingEventRepository;
        this.outboxService = outboxService;
        this.metrics = metrics;
    }

    @Transactional
    public EventPublishResult publish(PublishEventCommand cmd){
        if (cmd == null) throw new IllegalArgumentException("command is null");
        if (cmd.type() == null) throw new IllegalArgumentException("type is null");
        if (cmd.payload() == null || cmd.payload().isBlank()) throw new IllegalArgumentException("payload is blank");

        String source = (cmd.source() == null || cmd.source().isBlank()) ? "api" : cmd.source().trim();
        String producer = (cmd.producer() == null || cmd.producer().isBlank()) ? source : cmd.producer().trim();
        String externalEventId = (cmd.externalEventId() == null || cmd.externalEventId().isBlank())
                ? UUID.randomUUID().toString()
                : cmd.externalEventId().trim();
        String payload = cmd.payload().trim();
        String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID);

        var duplicate = incomingEventRepository.findByProducerAndExternalEventId(producer, externalEventId);
        if (duplicate.isPresent()) {
            metrics.incDuplicateEvent();
            IncomingEvent incoming = duplicate.get();
            log.info("Duplicate incoming event detected producer={} externalEventId={} incomingEventId={}",
                    producer, externalEventId, incoming.getId());
            return new EventPublishResult(incoming.getAppEvent(), incoming, true);
        }

        IncomingEvent incomingEvent = incomingEventRepository.save(
                new IncomingEvent(externalEventId, producer, cmd.type(), payload, correlationId)
        );

        AppEvent saved = eventRepository.save(new AppEvent(cmd.type(), payload, source));
        EventOccurredMessage msg = new EventOccurredMessage(
                saved.getType().name(),
                saved.getId().toString(),
                saved.getCreatedAt(),
                Map.of("payload", saved.getPayload(), "source", saved.getSource(), "incomingEventId", incomingEvent.getId().toString()),
                correlationId
        );
        outboxService.enqueue("AppEvent", saved.getId().toString(), OutboxService.EVENT_OCCURRED, msg);
        incomingEvent.markProcessed(saved);
        metrics.incAcceptedEvent();
        log.info("Incoming event accepted producer={} externalEventId={} incomingEventId={} appEventId={}",
                producer, externalEventId, incomingEvent.getId(), saved.getId());

        return new EventPublishResult(saved, incomingEvent, false);

    }
}
