package com.tosin.koins.common.exception;

/**
 * Used when a resource already exists or a duplicate operation is attempted.
 *
 * Example:
 * - Email already exists
 * - Phone number already exists
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
