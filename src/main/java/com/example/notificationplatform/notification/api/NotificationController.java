package com.example.notificationplatform.notification.api;

import com.example.notificationplatform.notification.api.dto.NotificationResponse;
import com.example.notificationplatform.notification.api.mapper.NotificationMapper;
import com.example.notificationplatform.notification.service.NotificationQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/notifications")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    public NotificationController(NotificationQueryService notificationQueryService) {
        this.notificationQueryService = notificationQueryService;
    }

    @GetMapping
    public List<NotificationResponse> listByUser(@PathVariable UUID userId) {
        return notificationQueryService.listByUser(userId).stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }
}
