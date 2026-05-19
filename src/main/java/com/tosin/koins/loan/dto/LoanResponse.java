package com.tosin.koins.loan.dto;

import com.tosin.koins.common.enums.LoanStatus;
import com.tosin.koins.loan.entity.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        UUID userId,
        BigDecimal loanAmount,
        BigDecimal interestRate,
        Integer durationDays,
        LoanStatus status,
        BigDecimal totalRepayableAmount,
        BigDecimal amountRepaid,
        BigDecimal outstandingAmount,
        LocalDate dueDate,
        List<RepaymentScheduleResponse> repaymentSchedule,
        LocalDateTime approvedAt,
        LocalDateTime disbursedAt,
        LocalDateTime repaidAt,
        LocalDateTime createdAt
) {

    /**
     * Use this when repayment schedule is not needed or not loaded.
     */
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getUserId(),
                loan.getLoanAmount(),
                loan.getInterestRate(),
                loan.getDurationDays(),
                loan.getStatus(),
                loan.getTotalRepayableAmount(),
                loan.getAmountRepaid(),
                loan.outstandingAmount(),
                loan.getDueDate(),
                List.of(),
                loan.getApprovedAt(),
                loan.getDisbursedAt(),
                loan.getRepaidAt(),
                loan.getCreatedAt()
        );
    }

    /**
     * Use this when repayment schedule has been loaded separately
     * from loan_repayment_schedules table.
     */
    public static LoanResponse from(
            Loan loan,
            List<RepaymentScheduleResponse> repaymentSchedule
    ) {
        return new LoanResponse(
                loan.getId(),
                loan.getUserId(),
                loan.getLoanAmount(),
                loan.getInterestRate(),
                loan.getDurationDays(),
                loan.getStatus(),
                loan.getTotalRepayableAmount(),
                loan.getAmountRepaid(),
                loan.outstandingAmount(),
                loan.getDueDate(),
                repaymentSchedule,
                loan.getApprovedAt(),
                loan.getDisbursedAt(),
                loan.getRepaidAt(),
                loan.getCreatedAt()
        );
    }
}