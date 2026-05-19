package com.tosin.koins.wallet.dto;

import java.math.BigDecimal;

public record WalletBalanceResponse(
        BigDecimal balance,
        String currency
) {
}
