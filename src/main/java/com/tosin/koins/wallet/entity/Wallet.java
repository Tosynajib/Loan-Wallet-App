package com.tosin.koins.wallet.entity;

import com.tosin.koins.common.enums.WalletStatus;
import com.tosin.koins.common.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the user's wallet.
 *
 * Important fintech rule:
 * - Use BigDecimal for money.
 * - Never use double/float for financial amounts.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }

        if (this.currency == null) {
            this.currency = "NGN";
        }

        if (this.status == null) {
            this.status = WalletStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void credit(BigDecimal amount) {
        validatePositiveAmount(amount);
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        validatePositiveAmount(amount);

        if (this.balance.compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient wallet balance");
        }

        this.balance = this.balance.subtract(amount);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }
    }
}