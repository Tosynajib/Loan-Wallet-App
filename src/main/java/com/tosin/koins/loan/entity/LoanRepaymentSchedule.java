package com.tosin.koins.loan.entity;

import com.tosin.koins.common.enums.RepaymentScheduleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents one repayment installment for a loan.
 *
 * We keep this in a separate table instead of JSON so we can:
 * - query overdue repayments
 * - update installment status
 * - send reminders
 * - track partial payments
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "loan_repayment_schedules",
        indexes = {
                @Index(name = "idx_repayment_schedule_loan_id", columnList = "loan_id"),
                @Index(name = "idx_repayment_schedule_due_date", columnList = "due_date"),
                @Index(name = "idx_repayment_schedule_status", columnList = "status")
        }
)
public class LoanRepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "loan_id", nullable = false)
    private UUID loanId;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "amount_due", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountDue;

    @Column(name = "amount_paid", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepaymentScheduleStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.amountPaid == null) {
            this.amountPaid = BigDecimal.ZERO;
        }

        if (this.status == null) {
            this.status = RepaymentScheduleStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void applyPayment(BigDecimal amount) {
        this.amountPaid = this.amountPaid.add(amount);

        if (this.amountPaid.compareTo(this.amountDue) >= 0) {
            this.status = RepaymentScheduleStatus.PAID;
        } else {
            this.status = RepaymentScheduleStatus.PARTIALLY_PAID;
        }
    }

    public void markOverdue() {
        if (this.status != RepaymentScheduleStatus.PAID) {
            this.status = RepaymentScheduleStatus.OVERDUE;
        }
    }
}