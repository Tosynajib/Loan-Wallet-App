package com.tosin.koins.auth.dto;

public record OtpResponse(String message,
                          int expiresInMinutes) {
}
