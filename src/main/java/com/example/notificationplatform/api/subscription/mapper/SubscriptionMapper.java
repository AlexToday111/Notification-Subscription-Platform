package com.example.notificationplatform.api.subscription.mapper;

import com.example.notificationplatform.api.subscription.dto.SubscriptionResponse;
import com.example.notificationplatform.domain.subscription.Subscription;

public final class SubscriptionMapper {

    private SubscriptionMapper() {}

    public static SubscriptionResponse toResponse(Subscription s) {
        return new SubscriptionResponse(
                s.getId(),
                s.getUser().getId(),
                s.getEventType(),
                s.getChannel(),
                s.getDestination(),
                s.isActive(),
                s.getCreatedAt()
        );
    }
}
