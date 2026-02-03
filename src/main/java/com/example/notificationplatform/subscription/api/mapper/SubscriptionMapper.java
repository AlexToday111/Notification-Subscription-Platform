package com.example.notificationplatform.subscription.api.mapper;

import com.example.notificationplatform.subscription.api.dto.SubscriptionResponse;
import com.example.notificationplatform.subscription.domain.Subscription;

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
