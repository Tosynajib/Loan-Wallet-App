package com.tosin.koins.wallet.dto;

import java.math.BigDecimal;

public record FundWalletResponse(
        String reference,
        BigDecimal amount,
        String currency,
        String status,
        String paymentAuthorizationUrl,
        String message
) {
}
