package com.tosin.koins.transaction.entity;

import com.tosin.koins.common.enums.TransactionStatus;
import com.tosin.koins.common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a financial transaction log.
 *
 * Important rule:
 * Every wallet money movement should have a transaction record.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_transactions_user_id", columnList = "user_id"),
                @Index(name = "idx_transactions_wallet_id", columnList = "wallet_id"),
                @Index(name = "idx_transactions_reference", columnList = "reference", unique = true)
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column
    private String description;

    @Column(name = "provider_reference")
    private String providerReference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = TransactionStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void markSuccessful() {
        this.status = TransactionStatus.SUCCESSFUL;
    }

    public void markFailed() {
        this.status = TransactionStatus.FAILED;
    }
}