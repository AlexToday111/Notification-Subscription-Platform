package com.example.notificationplatform.user.api;

import com.example.notificationplatform.user.api.dto.CreateUserRequest;
import com.example.notificationplatform.user.api.dto.UserResponse;
import com.example.notificationplatform.user.api.mapper.UserMapper;
import com.example.notificationplatform.user.domain.User;
import com.example.notificationplatform.user.service.UserService;
import com.example.notificationplatform.user.service.command.CreateUserCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req) {
        User created = userService.create(new CreateUserCommand(req.email(), req.email()));
        return ResponseEntity
                .created((URI.create("/api/users/" + created.getId)))
                .body(UserMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable UUID id) {
        return UserMapper.toResponse(userService.get(id));
    }
}