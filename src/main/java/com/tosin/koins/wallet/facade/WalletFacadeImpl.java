package com.tosin.koins.wallet.facade;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.common.response.ApplicationResponseFactory;
import com.tosin.koins.transaction.dto.TransactionResponse;
import com.tosin.koins.wallet.dto.FundWalletRequest;
import com.tosin.koins.wallet.dto.FundWalletResponse;
import com.tosin.koins.wallet.dto.WalletBalanceResponse;
import com.tosin.koins.wallet.dto.WalletResponse;
import com.tosin.koins.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WalletFacadeImpl implements WalletFacade {

    private final WalletService walletService;
    private final ApplicationResponseFactory responseFactory;

    @Override
    public ApiResponse<WalletResponse> getMyWallet() {
        WalletResponse response = walletService.getMyWallet();
        return responseFactory.success("Wallet retrieved successfully", response);
    }

    @Override
    public ApiResponse<WalletBalanceResponse> getMyWalletBalance() {
        WalletBalanceResponse response = walletService.getMyWalletBalance();
        return responseFactory.success("Wallet balance retrieved successfully", response);
    }

    @Override
    public ApiResponse<FundWalletResponse> fundWallet(FundWalletRequest request) {
        FundWalletResponse response = walletService.fundWallet(request);
        return responseFactory.success("Wallet funding initiated successfully", response);
    }

    @Override
    public ApiResponse<List<TransactionResponse>> getMyWalletTransactions() {
        List<TransactionResponse> response = walletService.getMyWalletTransactions();
        return responseFactory.success("Wallet transactions retrieved successfully", response);
    }
}