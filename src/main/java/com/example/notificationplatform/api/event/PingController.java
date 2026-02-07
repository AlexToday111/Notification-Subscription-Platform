package com.example.notificationplatform.api.event;

import com.example.notificationplatform.api.event.dto.PingResponse;
import com.example.notificationplatform.application.event.PingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PingController {
    private final PingService pingService;

    @GetMapping("/ping")
    public PingResponse ping(){
        return pingService.ping();
    }
}
