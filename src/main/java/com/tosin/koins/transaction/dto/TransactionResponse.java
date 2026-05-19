package com.tosin.koins.transaction.dto;

import com.tosin.koins.common.enums.TransactionStatus;
import com.tosin.koins.common.enums.TransactionType;
import com.tosin.koins.transaction.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(UUID id,
                                  UUID userId,
                                  UUID walletId,
                                  TransactionType type,
                                  BigDecimal amount,
                                  TransactionStatus status,
                                  String reference,
                                  String description,
                                  LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getWalletId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getReference(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}
