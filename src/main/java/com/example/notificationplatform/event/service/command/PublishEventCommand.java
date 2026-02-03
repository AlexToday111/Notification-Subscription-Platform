package com.example.notificationplatform.event.service.command;

import com.example.notificationplatform.event.domain.EventType;

public record PublishEventCommand(
        EventType type,
        String payload,
        String source
) { }
