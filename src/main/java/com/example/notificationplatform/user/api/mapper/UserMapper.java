package com.example.notificationplatform.user.api.mapper;

import com.example.notificationplatform.user.api.dto.UserResponse;
import com.example.notificationplatform.user.domain.User;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt()
        );
    }
}
