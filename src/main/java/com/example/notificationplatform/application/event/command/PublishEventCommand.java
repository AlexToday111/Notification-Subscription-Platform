package com.example.notificationplatform.application.event.command;

import com.example.notificationplatform.domain.event.EventType;

public record PublishEventCommand(
        EventType type,
        String payload,
        String source
) { }
