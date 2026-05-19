package com.tosin.koins.wallet.facade;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.transaction.dto.TransactionResponse;
import com.tosin.koins.wallet.dto.FundWalletRequest;
import com.tosin.koins.wallet.dto.FundWalletResponse;
import com.tosin.koins.wallet.dto.WalletBalanceResponse;
import com.tosin.koins.wallet.dto.WalletResponse;

import java.util.List;

public interface WalletFacade {

    ApiResponse<WalletResponse> getMyWallet();

    ApiResponse<WalletBalanceResponse> getMyWalletBalance();

    ApiResponse<FundWalletResponse> fundWallet(FundWalletRequest request);

    ApiResponse<List<TransactionResponse>> getMyWalletTransactions();
}