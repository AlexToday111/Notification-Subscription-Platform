package com.example.notificationplatform.infrastructure.messaging.consumer;

import com.example.notificationplatform.infrastructure.messaging.producer.DeliveryRequestMessage;
import com.example.notificationplatform.application.notification.NotificationDeliveryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryConsumer {

    private final NotificationDeliveryService deliveryService;

    @RabbitListener(queues = "delivery")
    public void consume(DeliveryRequestMessage message) {
        try {
            if (message.correlationId() != null) {
                MDC.put("correlationId", message.correlationId());
            }
            deliveryService.deliver(message.notificationId());
        } finally {
            MDC.remove("correlationId");
        }
    }
}
