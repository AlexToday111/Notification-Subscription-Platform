package com.example.notificationplatform.event.api;

import com.example.notificationplatform.event.api.dto.PingResponse;
import com.example.notificationplatform.event.service.PingService;
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
