package com.example.notificationplatform.subscription.service.command;

import com.example.notificationplatform.event.domain.EventType;
import com.example.notificationplatform.subscription.domain.Channel;

import java.util.UUID;

public record CreateSubscriptionCommand (
    UUID userId,
    EventType eventType,
    Channel channel,
    String destination
){}

