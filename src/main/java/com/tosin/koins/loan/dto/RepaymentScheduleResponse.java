package com.tosin.koins.loan.dto;

import com.tosin.koins.common.enums.RepaymentScheduleStatus;
import com.tosin.koins.loan.entity.LoanRepaymentSchedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RepaymentScheduleResponse(
        UUID id,
        Integer installmentNumber,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        LocalDate dueDate,
        RepaymentScheduleStatus status
) {

    public static RepaymentScheduleResponse from(LoanRepaymentSchedule schedule) {
        return new RepaymentScheduleResponse(
                schedule.getId(),
                schedule.getInstallmentNumber(),
                schedule.getAmountDue(),
                schedule.getAmountPaid(),
                schedule.getDueDate(),
                schedule.getStatus()
        );
    }
}