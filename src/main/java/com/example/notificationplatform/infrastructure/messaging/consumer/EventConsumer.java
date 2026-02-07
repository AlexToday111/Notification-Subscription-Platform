package com.example.notificationplatform.infrastructure.messaging.consumer;

import com.example.notificationplatform.infrastructure.config.RabbitConfig;
import com.example.notificationplatform.infrastructure.messaging.producer.EventOccurredMessage;
import com.example.notificationplatform.application.notification.NotificationGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final NotificationGeneratorService generator;

    @RabbitListener(queues = RabbitConfig.EVENTS_QUEUE)
    public void onEvent(EventOccurredMessage msg) {
        log.info("Received event from RabbitMQ: {}", msg);
        generator.handle(msg);
    }
}
