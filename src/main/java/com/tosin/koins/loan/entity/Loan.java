package com.tosin.koins.loan.entity;

import com.tosin.koins.common.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a user's loan application and repayment state.
 *
 * Important:
 * - Loan amount is the principal requested by the user.
 * - Total repayable amount = principal + interest.
 * - Amount repaid tracks how much the user has paid back.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "loans",
        indexes = {
                @Index(name = "idx_loans_user_id", columnList = "user_id"),
                @Index(name = "idx_loans_status", columnList = "status")
        }
)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "loan_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(name = "total_repayable_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalRepayableAmount;

    @Column(name = "amount_repaid", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountRepaid;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "disbursed_at")
    private LocalDateTime disbursedAt;

    @Column(name = "repaid_at")
    private LocalDateTime repaidAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = LoanStatus.PENDING;
        }

        if (this.amountRepaid == null) {
            this.amountRepaid = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal outstandingAmount() {
        return totalRepayableAmount.subtract(amountRepaid);
    }

    public boolean isFullyRepaid() {
        return amountRepaid.compareTo(totalRepayableAmount) >= 0;
    }
}