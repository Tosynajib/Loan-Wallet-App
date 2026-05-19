package com.tosin.koins.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ApplyLoanRequest(

        @NotNull(message = "Loan amount is required")
        @DecimalMin(value = "1000.00", message = "Minimum loan amount is 1000.00")
        BigDecimal amount,

        @NotNull(message = "Loan duration is required")
        @Positive(message = "Loan duration must be greater than zero")
        Integer durationDays
) {
}