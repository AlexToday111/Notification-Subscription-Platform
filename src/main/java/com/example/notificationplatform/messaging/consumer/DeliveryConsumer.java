package com.example.notificationplatform.messaging.consumer;

import com.example.notificationplatform.messaging.producer.DeliveryPublisher;
import com.example.notificationplatform.messaging.producer.DeliveryRequestMessage;
import com.example.notificationplatform.notification.service.delivery.NotificationDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryConsumer {

    private final NotificationDeliveryService notificationDeliveryService;

    @RabbitListener(queues = DeliveryPublisher.DELIVERY_QUEUE)
    public void consume(DeliveryRequestMessage message) {
        notificationDeliveryService.deliver(message.notificationId());
    }
}
