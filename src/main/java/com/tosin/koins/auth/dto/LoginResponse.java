package com.tosin.koins.auth.dto;

import com.tosin.koins.common.enums.UserRole;

import java.util.UUID;

public record LoginResponse(UUID userId,
                            String fullName,
                            String email,
                            UserRole role,
                            String accessToken,
                            String tokenType,
                            long expiresInMinutes
) {
}
