package com.example.notificationplatform.application.notification;

import com.example.notificationplatform.infrastructure.config.RabbitConfig;
import com.example.notificationplatform.infrastructure.metrics.NotificationMetrics;
import com.example.notificationplatform.infrastructure.messaging.producer.DeliveryRequestMessage;
import com.example.notificationplatform.infrastructure.messaging.producer.NotificationFailedMessage;
import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.infrastructure.persistence.notification.NotificationRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private static final int MAX_RETRIES = 5;
    private static final String DELIVERY_DLQ_QUEUE = "delivery.dlq";

    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationSenderRegistry senderRegistry;

    private final NotificationMetrics metrics;
    private final MeterRegistry meterRegistry;

    @Transactional
    public void deliver(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        if (n.isSent()) return;

        n.markSending();

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            senderRegistry
                    .get(n.getChannel())
                    .send(n);

            n.markSent();
            metrics.incSent();
        } catch (Exception e) {
            String err = e.getMessage();
            if (n.getRetryCount() + 1 < MAX_RETRIES) {
                n.markRetrying(err);
                metrics.incRetry();
                rabbitTemplate.convertAndSend(
                        RabbitConfig.DELIVERY_RETRY_QUEUE,
                        new DeliveryRequestMessage(n.getId())
                );
            } else {
                n.incrementRetry(err);
                n.markFailed(err);
                metrics.incFailed();

                rabbitTemplate.convertAndSend(
                        DELIVERY_DLQ_QUEUE,
                        NotificationFailedMessage.of(n.getId(), err, n.getRetryCount())
                );

                log.error("Notification {} moved to DLQ after {} retries. Reason={}",
                        n.getId(), n.getRetryCount(), err);
            }
        } finally {
            sample.stop(meterRegistry.timer("delivery.duration"));
        }
    }
}
