package com.example.notificationplatform.application.event;

import com.example.notificationplatform.domain.event.AppEvent;
import com.example.notificationplatform.domain.event.IncomingEvent;

public record EventPublishResult(
        AppEvent appEvent,
        IncomingEvent incomingEvent,
        boolean duplicate
) {
}
