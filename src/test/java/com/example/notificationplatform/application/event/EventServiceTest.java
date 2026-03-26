package com.example.notificationplatform.application.event;

import com.example.notificationplatform.application.event.command.PublishEventCommand;
import com.example.notificationplatform.application.outbox.OutboxService;
import com.example.notificationplatform.domain.event.AppEvent;
import com.example.notificationplatform.domain.event.EventType;
import com.example.notificationplatform.domain.event.IncomingEvent;
import com.example.notificationplatform.infrastructure.metrics.NotificationMetrics;
import com.example.notificationplatform.infrastructure.persistence.event.AppEventRepository;
import com.example.notificationplatform.infrastructure.persistence.event.IncomingEventRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private final AppEventRepository eventRepository = mock(AppEventRepository.class);
    private final IncomingEventRepository incomingEventRepository = mock(IncomingEventRepository.class);
    private final OutboxService outboxService = mock(OutboxService.class);
    private final NotificationMetrics metrics = mock(NotificationMetrics.class);
    private final EventService service = new EventService(eventRepository, incomingEventRepository, outboxService, metrics);

    @Test
    void publish_createsIncomingEventAppEventAndOutboxMessage() {
        when(incomingEventRepository.findByProducerAndExternalEventId("billing", "evt-1")).thenReturn(Optional.empty());
        when(incomingEventRepository.save(any(IncomingEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventRepository.save(any(AppEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventPublishResult result = service.publish(new PublishEventCommand(
                EventType.SYSTEM_MESSAGE,
                "{\"severity\":\"CRITICAL\"}",
                "api",
                "evt-1",
                "billing"
        ));

        assertFalse(result.duplicate());
        assertEquals("evt-1", result.incomingEvent().getExternalEventId());
        assertNotNull(result.appEvent());
        verify(outboxService).enqueue(eq("AppEvent"), anyString(), eq(OutboxService.EVENT_OCCURRED), any());
        verify(metrics).incAcceptedEvent();
    }

    @Test
    void publish_duplicateReturnsPredictableResultWithoutCreatingEvent() {
        IncomingEvent existing = new IncomingEvent("evt-1", "billing", EventType.SYSTEM_MESSAGE, "{}", "corr");
        when(incomingEventRepository.findByProducerAndExternalEventId("billing", "evt-1")).thenReturn(Optional.of(existing));

        EventPublishResult result = service.publish(new PublishEventCommand(
                EventType.SYSTEM_MESSAGE,
                "{}",
                "api",
                "evt-1",
                "billing"
        ));

        assertTrue(result.duplicate());
        verify(eventRepository, never()).save(any());
        verify(outboxService, never()).enqueue(anyString(), anyString(), anyString(), any());
        verify(metrics).incDuplicateEvent();
    }
}
