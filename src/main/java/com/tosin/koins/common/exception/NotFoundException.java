package com.tosin.koins.common.exception;

/**
 * Used when a requested resource does not exist.
 *
 * Example:
 * - User not found
 * - Wallet not found
 * - Loan not found
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
