package com.example.notificationplatform.application.outbox;

import com.example.notificationplatform.domain.outbox.OutboxMessage;
import com.example.notificationplatform.infrastructure.persistence.outbox.OutboxMessageRepository;
import com.example.notificationplatform.infrastructure.web.CorrelationIdFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    public static final String EVENT_OCCURRED = "EVENT_OCCURRED";
    public static final String DELIVERY_REQUESTED = "DELIVERY_REQUESTED";

    private final OutboxMessageRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxMessage enqueue(String aggregateType, String aggregateId, String eventType, Object payload) {
        try {
            return repository.save(OutboxMessage.pending(
                    aggregateType,
                    aggregateId,
                    eventType,
                    objectMapper.writeValueAsString(payload),
                    MDC.get(CorrelationIdFilter.CORRELATION_ID)
            ));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Outbox payload is not serializable", e);
        }
    }
}
