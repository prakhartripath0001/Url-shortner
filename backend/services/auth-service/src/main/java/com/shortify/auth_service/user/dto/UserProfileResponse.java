package com.shortify.auth_service.user.dto;

import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.enums.Role;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        Role role,
        LocalDateTime createdAt
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
