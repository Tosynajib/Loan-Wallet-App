package com.tosin.koins.wallet.dto;

import com.tosin.koins.common.enums.WalletStatus;
import com.tosin.koins.wallet.entity.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletResponse(UUID walletId,
                             UUID userId,
                             BigDecimal balance,
                             String currency,
                             WalletStatus status,
                             LocalDateTime createdAt
) {
    public static WalletResponse from(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getStatus(),
                wallet.getCreatedAt()
        );
    }
}
