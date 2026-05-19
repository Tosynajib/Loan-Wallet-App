package com.tosin.koins.wallet.service;

import com.tosin.koins.transaction.dto.TransactionResponse;
import com.tosin.koins.wallet.dto.*;

import java.util.List;

public interface WalletService {

    WalletResponse getMyWallet();

    WalletBalanceResponse getMyWalletBalance();

    FundWalletResponse fundWallet(FundWalletRequest request);

    List<TransactionResponse> getMyWalletTransactions();
}
