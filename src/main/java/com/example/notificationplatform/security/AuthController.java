package com.example.notificationplatform.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if ("admin".equals(req.username()) && "admin".equals(req.password())) {
            String token = jwtService.generate(req.username(), List.of("ADMIN"));
            return ResponseEntity.ok(Map.of("token", token));
        }
        if ("user".equals(req.username()) && "user".equals(req.password())) {
            String token = jwtService.generate(req.username(), List.of("USER"));
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(401).body(Map.of("error", "bad credentials"));
    }

    public record LoginRequest(String username, String password) {}
}
