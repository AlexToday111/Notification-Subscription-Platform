package com.example.notificationplatform.application.notification;

import com.example.notificationplatform.domain.event.AppEvent;
import com.example.notificationplatform.domain.event.EventType;
import com.example.notificationplatform.domain.notification.DeliveryAttempt;
import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.domain.notification.NotificationStatus;
import com.example.notificationplatform.domain.subscription.Channel;
import com.example.notificationplatform.domain.subscription.Subscription;
import com.example.notificationplatform.domain.user.User;
import com.example.notificationplatform.infrastructure.metrics.NotificationMetrics;
import com.example.notificationplatform.infrastructure.persistence.notification.DeliveryAttemptRepository;
import com.example.notificationplatform.infrastructure.persistence.notification.NotificationRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NotificationDeliveryServiceTest {

    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private final DeliveryAttemptRepository attemptRepository = mock(DeliveryAttemptRepository.class);
    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
    private final NotificationSenderRegistry senderRegistry = mock(NotificationSenderRegistry.class);
    private final NotificationMetrics metrics = mock(NotificationMetrics.class);
    private final NotificationDeliveryService service = new NotificationDeliveryService(
            notificationRepository,
            attemptRepository,
            rabbitTemplate,
            senderRegistry,
            metrics,
            new SimpleMeterRegistry()
    );

    @Test
    void deliver_successMarksNotificationSentAndAttemptSucceeded() {
        Notification notification = notification(Channel.EMAIL);
        NotificationSender sender = mock(NotificationSender.class);
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        when(attemptRepository.save(any(DeliveryAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(senderRegistry.get(Channel.EMAIL)).thenReturn(sender);
        when(sender.send(notification)).thenReturn(DeliveryResult.ok());

        service.deliver(notification.getId());

        assertEquals(NotificationStatus.SENT, notification.getStatus());
        verify(metrics).incSent();
    }

    @Test
    void deliver_retryableFailureSchedulesRetry() {
        Notification notification = notification(Channel.WEBHOOK);
        NotificationSender sender = mock(NotificationSender.class);
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        when(attemptRepository.save(any(DeliveryAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(senderRegistry.get(Channel.WEBHOOK)).thenReturn(sender);
        when(sender.send(notification)).thenThrow(new DeliveryException("WEBHOOK_5XX", "server error", 503, "down", true));

        service.deliver(notification.getId());

        assertEquals(NotificationStatus.RETRY_SCHEDULED, notification.getStatus());
        verify(metrics).incRetry();
        verify(rabbitTemplate).convertAndSend(eq(""), eq("delivery.retry"), any(), any(org.springframework.amqp.core.MessagePostProcessor.class));
    }

    private Notification notification(Channel channel) {
        User user = new User("delivery@example.com", "Delivery User");
        Subscription subscription = new Subscription(user, EventType.SYSTEM_MESSAGE, channel, destination(channel));
        AppEvent event = new AppEvent(EventType.SYSTEM_MESSAGE, "{\"severity\":\"CRITICAL\"}", "test");
        Notification notification = Notification.newFrom(event, subscription, "critical event", "corr-1");
        notification.markQueued();
        return notification;
    }

    private String destination(Channel channel) {
        return switch (channel) {
            case EMAIL -> "delivery@example.com";
            case TELEGRAM -> "123456";
            case WEBHOOK -> "http://localhost/webhook";
        };
    }
}
