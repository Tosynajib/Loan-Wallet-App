package com.tosin.koins.transaction.controller;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.transaction.dto.TransactionResponse;
import com.tosin.koins.transaction.facade.TransactionFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction history and transaction lookup APIs")
public class TransactionController {

    private final TransactionFacade transactionFacade;

    @GetMapping
    @Operation(summary = "Get authenticated user's transaction history")
    public ApiResponse<List<TransactionResponse>> getMyTransactions() {
        return transactionFacade.getMyTransactions();
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get a single transaction by ID")
    public ApiResponse<TransactionResponse> getMyTransactionById(
            @PathVariable UUID transactionId
    ) {
        return transactionFacade.getMyTransactionById(transactionId);
    }
}