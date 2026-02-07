package com.example.notificationplatform.application.subscription.command;

import com.example.notificationplatform.domain.event.EventType;
import com.example.notificationplatform.domain.subscription.Channel;

import java.util.UUID;

public record CreateSubscriptionCommand (
    UUID userId,
    EventType eventType,
    Channel channel,
    String destination
){}

