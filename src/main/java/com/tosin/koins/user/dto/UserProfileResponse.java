package com.tosin.koins.user.dto;

import com.tosin.koins.common.enums.AccountStatus;
import com.tosin.koins.common.enums.UserRole;
import com.tosin.koins.user.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String fullName,
        String email,
        String phoneNumber,
        UserRole role,
        AccountStatus status,
        LocalDateTime createdAt
) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}