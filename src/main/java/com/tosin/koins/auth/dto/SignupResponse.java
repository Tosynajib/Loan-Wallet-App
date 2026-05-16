package com.tosin.koins.auth.dto;

import java.util.UUID;

public record SignupResponse(UUID userId,
                             UUID walletId,
                             String fullName,
                             String email,
                             String message
) {
}
