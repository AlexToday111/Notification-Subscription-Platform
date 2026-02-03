package com.example.notificationplatform.notification.api.mapper;

import com.example.notificationplatform.notification.api.dto.NotificationResponse;
import com.example.notificationplatform.notification.domain.Notification;

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
