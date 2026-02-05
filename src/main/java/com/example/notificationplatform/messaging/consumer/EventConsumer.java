package com.example.notificationplatform.messaging.consumer;

import com.example.notificationplatform.config.RabbitConfig;
import com.example.notificationplatform.messaging.producer.EventOccurredMessage;
import com.example.notificationplatform.notification.service.generation.NotificationGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final NotificationGeneratorService generator; // твой сервис из Day 2/3

    @RabbitListener(queues = RabbitConfig.EVENTS_QUEUE)
    public void onEvent(EventOccurredMessage msg) {
        log.info("Received event from RabbitMQ: {}", msg);
        generator.handle(msg);
    }
}

