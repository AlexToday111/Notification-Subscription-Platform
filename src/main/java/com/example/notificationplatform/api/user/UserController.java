package com.example.notificationplatform.api.user;

import com.example.notificationplatform.api.user.dto.CreateUserRequest;
import com.example.notificationplatform.api.user.dto.UserResponse;
import com.example.notificationplatform.api.user.mapper.UserMapper;
import com.example.notificationplatform.domain.user.User;
import com.example.notificationplatform.application.user.UserService;
import com.example.notificationplatform.application.user.command.CreateUserCommand;
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

        CreateUserCommand cmd = new CreateUserCommand(req.email(), req.name());

        User created = userService.create(cmd);

        return ResponseEntity
                .created(URI.create("/api/users/" + created.getId()))
                .body(UserMapper.toResponse(created));
    }


    @GetMapping("/{id}")
    public UserResponse get(@PathVariable UUID id) {
        return UserMapper.toResponse(userService.get(id));
    }
}