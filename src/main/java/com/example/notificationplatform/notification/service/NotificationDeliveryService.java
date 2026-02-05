package com.example.notificationplatform.notification.service;

import com.example.notificationplatform.messaging.producer.DeliveryPublisher;
import com.example.notificationplatform.messaging.producer.NotificationFailedMessage;
import com.example.notificationplatform.notification.domain.Notification;
import com.example.notificationplatform.notification.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {
    private static final int MAX_RETRIES = 5;
    private static final String DELIVERY_DLQ_QUEUE = "delivery.dlq";

    private final NotificationRepository notificationRepository;

    private final DeliveryPublisher deliveryPublisher;

    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void deliver(UUID notificationID) {
        Notification n = notificationRepository.findById(notificationID)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationID));

        if (n.isSent()) return;

        n.markSending();

        try {
            n.markSent();
        } catch (Exception e) {
            n.incrementRetry(e.getMessage());

            if (n.getRetryCount() < MAX_RETRIES) {
                n.markRetrying(e.getMessage());
                deliveryPublisher.publish(n.getId());
            } else {
                n.markFailed(e.getMessage());
                rabbitTemplate.convertAndSend(DELIVERY_DLQ_QUEUE,
                        NotificationFailedMessage.of(n.getId(), e.getMessage(), n.getRetryCount()));
            }
        }
    }
}
