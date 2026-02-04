package com.example.notificationplatform.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.notificationplatform.config.RabbitConfig;
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
