package com.example.notificationplatform.subscription.api;

import com.example.notificationplatform.subscription.api.dto.CreateSubscriptionRequest;
import com.example.notificationplatform.subscription.api.dto.SubscriptionResponse;
import com.example.notificationplatform.subscription.api.mapper.SubscriptionMapper;
import com.example.notificationplatform.subscription.domain.Subscription;
import com.example.notificationplatform.subscription.service.SubscriptionService;
import com.example.notificationplatform.subscription.service.command.CreateSubscriptionCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody CreateSubscriptionRequest req) {

        CreateSubscriptionCommand cmd = new CreateSubscriptionCommand(
                req.userId(),
                req.eventType(),
                req.channel(),
                req.destination()
        );

        Subscription created = subscriptionService.create(cmd);

        return ResponseEntity
                .created(URI.create("/api/subscriptions/" + created.getId()))
                .body(SubscriptionMapper.toResponse(created));
    }


    @GetMapping("/users/{userId}/subscriptions")
    public List<SubscriptionResponse> listByUser(@PathVariable UUID userId) {
        return subscriptionService.listByUser(userId).stream()
                .map(SubscriptionMapper::toResponse)
                .toList();
    }
}
