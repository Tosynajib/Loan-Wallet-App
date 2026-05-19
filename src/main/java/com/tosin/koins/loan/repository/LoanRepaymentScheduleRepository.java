package com.tosin.koins.loan.repository;

import com.tosin.koins.common.enums.RepaymentScheduleStatus;
import com.tosin.koins.loan.entity.LoanRepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepaymentScheduleRepository extends JpaRepository<LoanRepaymentSchedule, UUID> {

    List<LoanRepaymentSchedule> findByLoanIdOrderByInstallmentNumberAsc(UUID loanId);

    Optional<LoanRepaymentSchedule> findFirstByLoanIdAndStatusInOrderByInstallmentNumberAsc(
            UUID loanId,
            List<RepaymentScheduleStatus> statuses
    );

    List<LoanRepaymentSchedule> findByStatusInAndDueDateBefore(
            List<RepaymentScheduleStatus> statuses,
            LocalDate date
    );

    List<LoanRepaymentSchedule> findByStatusInAndDueDate(
            List<RepaymentScheduleStatus> statuses,
            LocalDate date
    );
}