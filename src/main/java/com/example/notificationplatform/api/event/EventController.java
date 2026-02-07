package com.example.notificationplatform.api.event;

import com.example.notificationplatform.api.event.dto.AppEventResponse;
import com.example.notificationplatform.api.event.dto.PublishEventRequest;
import com.example.notificationplatform.api.event.mapper.AppEventMapper;
import com.example.notificationplatform.domain.event.AppEvent;
import com.example.notificationplatform.application.event.EventService;
import com.example.notificationplatform.application.event.command.PublishEventCommand;
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

        PublishEventCommand cmd = new PublishEventCommand(
                req.type(),
                req.payload(),
                req.source()
        );

        AppEvent created = eventService.publish(cmd);

        return ResponseEntity
                .created(URI.create("/api/events/" + created.getId()))
                .body(AppEventMapper.toResponse(created));
    }

}
