package com.tosin.koins.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FundWalletRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "100.00", message = "Minimum wallet funding amount is 100.00")
        BigDecimal amount
) {
}
