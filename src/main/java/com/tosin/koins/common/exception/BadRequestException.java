package com.tosin.koins.common.exception;

/**
 * Used when the client sends invalid data or breaks a business rule.
 *
 * Example:
 * - Loan amount exceeds allowed limit
 * - Wallet has insufficient balance
 */
public class BadRequestException extends RuntimeException{

    public BadRequestException(String message) {
        super(message);
    }
}
