package com.example.notificationplatform.api.notification;

import com.example.notificationplatform.api.notification.dto.NotificationResponse;
import com.example.notificationplatform.api.notification.mapper.NotificationMapper;
import com.example.notificationplatform.application.notification.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> listByUser(@PathVariable UUID userId) {
        return notificationService.listByUser(userId).stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }
}
