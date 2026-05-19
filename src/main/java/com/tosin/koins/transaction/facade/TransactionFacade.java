package com.tosin.koins.transaction.facade;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.transaction.dto.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionFacade {

    ApiResponse<List<TransactionResponse>> getMyTransactions();

    ApiResponse<TransactionResponse> getMyTransactionById(UUID transactionId);

}
