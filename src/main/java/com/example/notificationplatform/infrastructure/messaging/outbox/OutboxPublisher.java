package com.example.notificationplatform.infrastructure.messaging.outbox;

import com.example.notificationplatform.application.outbox.OutboxService;
import com.example.notificationplatform.domain.outbox.OutboxMessage;
import com.example.notificationplatform.domain.outbox.OutboxStatus;
import com.example.notificationplatform.infrastructure.config.RabbitConfig;
import com.example.notificationplatform.infrastructure.messaging.producer.DeliveryRequestMessage;
import com.example.notificationplatform.infrastructure.messaging.producer.EventOccurredMessage;
import com.example.notificationplatform.infrastructure.persistence.outbox.OutboxMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxMessageRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${app.outbox.publisher-delay-ms:1000}")
    @Transactional
    public void publishPending() {
        repository.findRetryableFailures().forEach(OutboxMessage::retry);
        repository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)
                .forEach(this::publishOne);
    }

    private void publishOne(OutboxMessage message) {
        try {
            if (message.getCorrelationId() != null) {
                MDC.put("correlationId", message.getCorrelationId());
            }
            if (OutboxService.EVENT_OCCURRED.equals(message.getEventType())) {
                EventOccurredMessage payload = objectMapper.readValue(message.getPayload(), EventOccurredMessage.class);
                rabbitTemplate.convertAndSend(RabbitConfig.EVENTS_EXCHANGE, RabbitConfig.EVENTS_ROUTING_KEY, payload, m -> {
                    m.getMessageProperties().setHeader("correlationId", message.getCorrelationId());
                    return m;
                });
            } else if (OutboxService.DELIVERY_REQUESTED.equals(message.getEventType())) {
                DeliveryRequestMessage payload = objectMapper.readValue(message.getPayload(), DeliveryRequestMessage.class);
                rabbitTemplate.convertAndSend(RabbitConfig.DELIVERY_QUEUE, payload, m -> {
                    m.getMessageProperties().setHeader("correlationId", message.getCorrelationId());
                    return m;
                });
            } else {
                throw new IllegalStateException("Unsupported outbox event type: " + message.getEventType());
            }
            message.markPublished();
            log.info("Outbox message published id={} eventType={} aggregateId={}",
                    message.getId(), message.getEventType(), message.getAggregateId());
        } catch (Exception e) {
            message.markFailed(e.getMessage());
            log.warn("Outbox publication failed id={} eventType={} retryCount={}",
                    message.getId(), message.getEventType(), message.getRetryCount(), e);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
