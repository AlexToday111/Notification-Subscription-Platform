package com.example.notificationplatform.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EVENTS_EXCHANGE = "app.events";
    public static final String EVENTS_QUEUE = "events.queue";
    public static final String EVENTS_ROUTING_KEY = "event.occurred";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue eventsQueue() {
        return QueueBuilder.durable(EVENTS_QUEUE).build();
    }

    @Bean
    public Binding eventsBinding(Queue eventsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(eventsQueue).to(eventsExchange).with("event.*");
    }
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}


