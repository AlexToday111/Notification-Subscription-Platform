package com.example.notificationplatform.event.api.mapper;

import com.example.notificationplatform.event.api.dto.AppEventResponse;
import com.example.notificationplatform.event.domain.AppEvent;

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
