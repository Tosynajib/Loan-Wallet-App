package com.tosin.koins.integration.payment.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published after a wallet funding payment is confirmed.
 *
 * This can be used for audit, notification, analytics, reconciliation, etc.
 */
public record PaymentConfirmationEvent(
        UUID userId,
        UUID walletId,
        UUID transactionId,
        String transactionReference,
        String providerReference,
        BigDecimal amount,
        String currency,
        LocalDateTime confirmedAt
) {
}