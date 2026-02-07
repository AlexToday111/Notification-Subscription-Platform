package com.example.notificationplatform.api.notification.mapper;

import com.example.notificationplatform.api.notification.dto.NotificationResponse;
import com.example.notificationplatform.domain.notification.Notification;

import java.util.UUID;

public final class NotificationMapper {

    private NotificationMapper() {}

    public static NotificationResponse toResponse(Notification n) {
        UUID subscriptionId = (n.getSubscription() == null) ? null : n.getSubscription().getId();

        return new NotificationResponse(
                n.getId(),
                n.getUser().getId(),
                n.getEvent().getId(),
                subscriptionId,
                n.getChannel(),
                n.getDestination(),
                n.getStatus(),
                n.getContent(),
                n.getCreatedAt()
        );
    }
}
