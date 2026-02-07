package com.example.notificationplatform.api.event.mapper;

import com.example.notificationplatform.api.event.dto.AppEventResponse;
import com.example.notificationplatform.domain.event.AppEvent;

public final class AppEventMapper {

    private AppEventMapper() {}

    public static AppEventResponse toResponse(AppEvent e) {
        return new AppEventResponse(
                e.getId(),
                e.getType(),
                e.getPayload(),
                e.getSource(),
                e.getCreatedAt()
        );
    }
}
