package com.example.notificationplatform.infrastructure.messaging.producer;

import com.example.notificationplatform.infrastructure.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryPublisher {
    public static final String DELIVERY_QUEUE = "delivery";

    private final RabbitTemplate rabbitTemplate;

    public void publish(UUID notificationId){
        rabbitTemplate.convertAndSend(RabbitConfig.DELIVERY_QUEUE, new DeliveryRequestMessage(notificationId));
    }
}
