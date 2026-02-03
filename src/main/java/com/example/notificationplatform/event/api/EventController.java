package com.example.notificationplatform.event.api;

import com.example.notificationplatform.event.api.dto.AppEventResponse;
import com.example.notificationplatform.event.api.dto.PublishEventRequest;
import com.example.notificationplatform.event.api.mapper.AppEventMapper;
import com.example.notificationplatform.event.domain.AppEvent;
import com.example.notificationplatform.event.service.EventService;
import com.example.notificationplatform.event.service.command.PublishEventCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<AppEventResponse> publish(@Valid @RequestBody PublishEventRequest req) {
        AppEvent created = eventService.publish(new PublishEventCommand(req.type(), req.payload(), req.source()));

        return ResponseEntity
                .created(URI.create("/api/events/" + created.getId()))
                .body(AppEventMapper.toResponse(created));
    }
}
