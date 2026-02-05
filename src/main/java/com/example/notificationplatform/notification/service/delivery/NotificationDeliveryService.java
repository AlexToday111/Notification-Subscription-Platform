package com.example.notificationplatform.notification.service.delivery;

import com.example.notificationplatform.config.RabbitConfig;
import com.example.notificationplatform.messaging.producer.DeliveryPublisher;
import com.example.notificationplatform.messaging.producer.DeliveryRequestMessage;
import com.example.notificationplatform.messaging.producer.NotificationFailedMessage;
import com.example.notificationplatform.notification.domain.Notification;
import com.example.notificationplatform.notification.repo.NotificationRepository;
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
    private final DeliveryPublisher deliveryPublisher;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationSenderRegistry senderRegistry;

    @Transactional
    public void deliver(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        if (n.isSent()) return;

        n.markSending();

        try {
            senderRegistry
                    .get(n.getChannel())
                    .send(n);

            n.markSent();
        } catch (Exception e) {
            String err = e.getMessage();
            if (n.getRetryCount() + 1 < MAX_RETRIES) {
                n.markRetrying(err);
                rabbitTemplate.convertAndSend(
                        RabbitConfig.DELIVERY_RETRY_QUEUE,
                        new DeliveryRequestMessage(n.getId())
                );
            } else {
                n.incrementRetry(err);
                n.markFailed(err);
                n.markFailed(e.getMessage());
                rabbitTemplate.convertAndSend(DELIVERY_DLQ_QUEUE,
                        NotificationFailedMessage.of(n.getId(), err, n.getRetryCount()));
                log.error(
                        "Notification {} moved to DLQ after {} retries. Reason={}",
                        n.getId(),
                        n.getRetryCount(),
                        err
                );
            }
        }
    }
}
