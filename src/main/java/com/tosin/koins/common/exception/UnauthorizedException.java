package com.tosin.koins.common.exception;

/**
 * Used when authentication fails or a request is not trusted.
 *
 * Example:
 * - Invalid JWT
 * - Invalid Paystack webhook signature
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
