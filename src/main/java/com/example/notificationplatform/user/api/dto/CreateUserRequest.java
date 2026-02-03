package com.example.notificationplatform.user.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @Email @NotBlank @Size(max = 320) String email,
        @NotBlank @Size(max = 120) String name
) {}
