package com.tosin.koins.common.response;

import org.springframework.stereotype.Component;

/**
 * Central factory for creating standard API responses.
 *
 * Why we use this:
 * - Keeps controllers clean.
 * - Keeps services focused on business logic.
 * - Ensures all API responses follow the same structure.
 */
@Component
public class ApplicationResponseFactory {

    public <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.success(message, data);
    }

    public <T> ApiResponse<T> success(String message) {
        return ApiResponse.success(message);
    }

    public <T> ApiResponse<T> failure(String message, T data) {
        return ApiResponse.failure(message, data);
    }

    public <T> ApiResponse<T> failure(String message) {
        return ApiResponse.failure(message);
    }
}