package com.example.notificationplatform.api.event;

import com.example.notificationplatform.api.event.dto.PublishEventResponse;
import com.example.notificationplatform.api.event.dto.PublishEventRequest;
import com.example.notificationplatform.application.event.EventPublishResult;
import com.example.notificationplatform.application.event.EventService;
import com.example.notificationplatform.application.event.command.PublishEventCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<PublishEventResponse> publish(@Valid @RequestBody PublishEventRequest req) {

        PublishEventCommand cmd = new PublishEventCommand(
                req.type(),
                req.payload(),
                req.source(),
                req.externalEventId(),
                req.producer()
        );

        EventPublishResult result = eventService.publish(cmd);
        var event = result.appEvent();
        var incoming = result.incomingEvent();
        PublishEventResponse body = new PublishEventResponse(
                event == null ? null : event.getId(),
                incoming.getId(),
                incoming.getExternalEventId(),
                incoming.getProducer(),
                incoming.getType(),
                incoming.getPayload(),
                event == null ? null : event.getSource(),
                event == null ? null : event.getCreatedAt(),
                result.duplicate(),
                incoming.getStatus().name()
        );

        if (result.duplicate()) {
            return ResponseEntity.status(HttpStatus.OK).body(body);
        }
        return ResponseEntity.created(URI.create("/api/events/" + event.getId())).body(body);
    }

}
