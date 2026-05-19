package com.tosin.koins.loan.dto;

import java.util.UUID;

public record LoanActionResponse(
        UUID loanId,
        String status,
        String message
) {
}