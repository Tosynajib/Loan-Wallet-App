package com.tosin.koins.transaction.facade;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.common.response.ApplicationResponseFactory;
import com.tosin.koins.transaction.dto.TransactionResponse;
import com.tosin.koins.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionFacadeImpl implements TransactionFacade {

    private final TransactionService transactionService;
    private final ApplicationResponseFactory responseFactory;

    @Override
    public ApiResponse<List<TransactionResponse>> getMyTransactions() {
        List<TransactionResponse> response = transactionService.getMyTransactions();
        return responseFactory.success("Transactions retrieved successfully", response);
    }

    @Override
    public ApiResponse<TransactionResponse> getMyTransactionById(UUID transactionId) {
        TransactionResponse response = transactionService.getMyTransactionById(transactionId);
        return responseFactory.success("Transaction retrieved successfully", response);
    }
}