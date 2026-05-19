package com.tosin.koins.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RepayLoanRequest(

        @NotNull(message = "Repayment amount is required")
        @DecimalMin(value = "100.00", message = "Minimum repayment amount is 100.00")
        BigDecimal amount
) {
}