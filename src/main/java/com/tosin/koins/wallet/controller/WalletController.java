package com.tosin.koins.wallet.controller;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.transaction.dto.TransactionResponse;
import com.tosin.koins.wallet.dto.FundWalletRequest;
import com.tosin.koins.wallet.dto.FundWalletResponse;
import com.tosin.koins.wallet.dto.WalletBalanceResponse;
import com.tosin.koins.wallet.dto.WalletResponse;
import com.tosin.koins.wallet.facade.WalletFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Wallet controller.
 *
 * Plain controller:
 * - accepts request
 * - delegates to facade
 * - does not contain business logic
 */
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "Wallet balance, funding and transaction history APIs")
public class WalletController {

    private final WalletFacade walletFacade;

    @GetMapping("/me")
    @Operation(summary = "Get authenticated user's wallet")
    public ApiResponse<WalletResponse> getMyWallet() {
        return walletFacade.getMyWallet();
    }

    @GetMapping("/balance")
    @Operation(summary = "Get authenticated user's wallet balance")
    public ApiResponse<WalletBalanceResponse> getMyWalletBalance() {
        return walletFacade.getMyWalletBalance();
    }

    @PostMapping("/fund")
    @Operation(summary = "Create a wallet funding transaction")
    public ApiResponse<FundWalletResponse> fundWallet(
            @Valid @RequestBody FundWalletRequest request
    ) {
        return walletFacade.fundWallet(request);
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get authenticated user's wallet transaction history")
    public ApiResponse<List<TransactionResponse>> getMyWalletTransactions() {
        return walletFacade.getMyWalletTransactions();
    }
}