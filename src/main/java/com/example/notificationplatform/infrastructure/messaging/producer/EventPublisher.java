package com.example.notificationplatform.infrastructure.messaging.producer;

import com.example.notificationplatform.infrastructure.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publish(EventOccurredMessage msg) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                RabbitConfig.EVENTS_ROUTING_KEY,
                msg
        );
    }
}

